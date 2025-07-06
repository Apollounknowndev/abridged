package dev.worldgen.abridged.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.abridged.config.ConfigHandler;
import dev.worldgen.abridged.registry.AbridgedRegistries;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

import java.util.*;
import java.util.stream.IntStream;

public class BridgeStructure extends Structure {
    public static final MapCodec<BridgeStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        settingsCodec(instance),
        Codec.INT.fieldOf("max_chunk_radius").forGetter(BridgeStructure::maxChunkRadius)
    ).apply(instance, BridgeStructure::new));

    private final int maxChunkRadius;

    protected BridgeStructure(StructureSettings baseSettings, int maxChunkRadius) {
        super(baseSettings);
        this.maxChunkRadius = maxChunkRadius;
    }

    public int maxChunkRadius() {
        return maxChunkRadius;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (context.random().nextFloat() > ConfigHandler.state().frequency) return Optional.empty();

        BlockPos origin = new BlockPos(context.chunkPos().getMiddleBlockX(), 90, context.chunkPos().getMiddleBlockZ());
        Heightmaps heightmaps = buildHeightmapData(context, origin);

        List<Reference<BridgeConfig>> configs = context.registryAccess().lookupOrThrow(AbridgedRegistries.BRIDGE_CONFIG_KEY).listElements().toList();

        List<Integer> indices = Util.toShuffledList(IntStream.rangeClosed(0, configs.size() - 1), context.random());
        for (int index : indices) {
            Holder<BridgeConfig> config = configs.get(index);
            BridgeData bridgeData = getBridgeData(context, origin, heightmaps, config.value());
            if (bridgeData != null) {
                BlockPos pos = new BlockPos(context.chunkPos().getMiddleBlockX(), bridgeData.getHeight(), context.chunkPos().getMiddleBlockZ());
                return onTopOfChunkCenter(context, Heightmap.Types.OCEAN_FLOOR_WG, collector -> addPieces(context.structureTemplateManager(), context.random(), pos, collector, bridgeData, config));
            }
        }
        return Optional.empty();
    }

    private BridgeData getBridgeData(GenerationContext context, BlockPos pos, Heightmaps heightmaps, BridgeConfig config) {
        if (!config.condition().test(context, pos)) return null;

        var xBridgeData = findValidSegmentLayout(Direction.EAST, heightmaps, config);
        return xBridgeData != null ? xBridgeData : findValidSegmentLayout(Direction.SOUTH, heightmaps, config);
    }

    private BridgeData findValidSegmentLayout(Direction direction, Heightmaps heightmaps, BridgeConfig config) {
        List<Integer> leftHeights = heightmaps.heights().get(direction.getOpposite());
        List<Integer> rightHeights = heightmaps.heights().get(direction);

        Integer negativeHeight = null;
        Integer positiveHeight = null;
        Integer chunkOffset = null;
        Integer totalSegments = null;


        search:
        for (int k = 0; k < leftHeights.size(); k++) {
            Integer leftHeight = leftHeights.get(k);
            if (!config.height().isValueInRange(leftHeight)) continue;

            for (int l = 0; l < rightHeights.size(); l++) {
                Integer rightHeight = rightHeights.get(l);
                if (!config.height().isValueInRange(rightHeight)) continue;

                int segments = k + l + 2;
                if (Math.abs(leftHeight - rightHeight) <= config.maxHeightDifference() && config.segments().isValueInRange(segments)) {
                    negativeHeight = leftHeight;
                    positiveHeight = rightHeight;
                    chunkOffset = -k - 1;
                    totalSegments = segments;
                    break search;
                }
            }
        }

        if (negativeHeight == null) return null;

        return new BridgeData(negativeHeight, positiveHeight, chunkOffset, totalSegments, direction);
    }

    private Heightmaps buildHeightmapData(GenerationContext context, BlockPos pos) {
        return new Heightmaps(new EnumMap<>(Map.of(
            Direction.NORTH, buildHeightmapList(context, pos, Direction.NORTH),
            Direction.EAST, buildHeightmapList(context, pos, Direction.EAST),
            Direction.SOUTH, buildHeightmapList(context, pos, Direction.SOUTH),
            Direction.WEST, buildHeightmapList(context, pos, Direction.WEST)
        )));
    }

    private List<Integer> buildHeightmapList(GenerationContext context, BlockPos pos, Direction direction) {
        List<Integer> heights = new ArrayList<>();
        for(int j = 1; j <= this.maxChunkRadius; j++) {
            int height = getHeightmap(pos.relative(direction, 16 * j), context);
            if (j != 1 && heights.get(heights.size() - 1) > height) {
                break;
            }
            heights.add(height);
        }
        return heights;
    }

    private static int getHeightmap(BlockPos pos, GenerationContext context) {
        ChunkGenerator generator = context.chunkGenerator();
        if (generator instanceof NoiseBasedChunkGenerator && !ConfigHandler.state().directlySampleHeightmap) {
            double depthAtSeaLevel = context.randomState().router().depth().compute(new DensityFunction.SinglePointContext(pos.getX(), 64, pos.getZ()));
            return (int) ((depthAtSeaLevel + 0.5) * 128) + 2;
        }
        return generator.getFirstFreeHeight(pos.getX(), pos.getZ(), Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
    }

    public static void addPieces(StructureTemplateManager manager, RandomSource random, BlockPos pos, StructurePieceAccessor pieceAccessor, BridgeStructure.BridgeData bridgeData, Holder<BridgeConfig> holder) {
        BridgeConfig config = holder.value();
        for (int i = 0; i < bridgeData.totalSegments(); i++) {
            if (i == 0) {
                pieceAccessor.addPiece(new BridgePiece(manager, getId(config.edge(), random), pos.relative(bridgeData.direction(), (bridgeData.chunkOffset()+i)*16), bridgeData.getRotation(), holder));
            } else if (i == bridgeData.totalSegments() - 1) {
                pieceAccessor.addPiece(new BridgePiece(manager, getId(config.edge(), random), pos.relative(bridgeData.direction(), (bridgeData.chunkOffset()+i)*16+15), bridgeData.getRotation(), Mirror.FRONT_BACK, holder));
            } else {
                pieceAccessor.addPiece(new BridgePiece(manager, getId(config.base(), random), pos.relative(bridgeData.direction(), (bridgeData.chunkOffset()+i)*16), bridgeData.getRotation(), holder));
            }
        }
        pieceAccessor.addPiece(new BridgePiece(manager, BridgePiece.BEARD_BASE, pos.relative(bridgeData.direction(), bridgeData.chunkOffset()*16).relative(Direction.DOWN, -1), bridgeData.getRotation(), holder));
        pieceAccessor.addPiece(new BridgePiece(manager, BridgePiece.BEARD_BASE, pos.relative(bridgeData.direction(), (bridgeData.chunkOffset()+bridgeData.totalSegments())*16-8).relative(Direction.DOWN, -1), bridgeData.getRotation(), holder));
    }

    private static ResourceLocation getId(List<ResourceLocation> ids, RandomSource random) {
        return Util.getRandom(ids, random);
    }

    @Override
    public void afterPlace(WorldGenLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox box, ChunkPos chunkPos, PiecesContainer piecesContainer) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int minY = world.dimensionType().minY();
        BoundingBox blockBox = piecesContainer.calculateBoundingBox();
        int baseY = blockBox.minY();

        List<StructurePiece> pieces = piecesContainer.pieces();
        if (pieces.isEmpty() || !(pieces.get(0) instanceof BridgePiece)) return;
        ResourceLocation configId = ((BridgePiece) pieces.get(0)).configId;

        Optional<Reference<BridgeConfig>> config = world.registryAccess().lookupOrThrow(AbridgedRegistries.BRIDGE_CONFIG_KEY).get(ResourceKey.create(AbridgedRegistries.BRIDGE_CONFIG_KEY, configId));

        if (config.isEmpty()) return;

        for(BridgeConfig.Extension extension : config.get().value().extensions()) {
            for(int x = box.minX(); x <= box.maxX(); ++x) {
                for(int z = box.minZ(); z <= box.maxZ(); ++z) {
                    mutable.set(x, baseY, z);
                    BlockState state = world.getBlockState(mutable);
                    if (contains(extension.blocks(), state)) {
                        if (!world.isEmptyBlock(mutable) && blockBox.isInside(mutable) && piecesContainer.isInsidePiece(mutable)) {
                            for(int y = baseY - 1; y > minY; --y) {
                                mutable.setY(y);
                                if (!(world.isEmptyBlock(mutable) || world.containsAnyLiquid(AABB.unitCubeFromLowerCorner(mutable.getCenter())) || contains(List.of(Blocks.GRASS_BLOCK), state))) {
                                    break;
                                }

                                world.setBlock(mutable, extension.extendedState().getState(random, mutable), 3);
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean contains(List<Block> blocks, BlockState state) {
        for (Block block : blocks) {
            if (Objects.equals(block.defaultBlockState(), state)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public StructureType<?> type() {
        return AbridgedRegistries.BRIDGE_STRUCTURE;
    }

    public record Heightmaps(EnumMap<Direction, List<Integer>> heights) {
    }

    public record BridgeData(Integer leftHeight, Integer rightHeight, Integer chunkOffset, Integer totalSegments, Direction direction) {
        public Rotation getRotation() {
            return this.direction.getAxis() == Direction.Axis.X ? Rotation.NONE : Rotation.CLOCKWISE_90;
        }
        public Integer getHeight() {
            return Math.min(this.leftHeight, this.rightHeight);
        }
    }
}

