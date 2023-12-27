package dev.worldgen.abridged.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.abridged.config.ConfigHandler;
import dev.worldgen.abridged.platform.Services;
import dev.worldgen.abridged.registry.AbridgedRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BridgeStructure extends Structure {
    public static final Codec<BridgeStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        settingsCodec(instance),
        InclusiveRange.codec(Codec.INT).fieldOf("height").forGetter(BridgeStructure::height),
        Codec.INT.fieldOf("max_height_difference").forGetter(BridgeStructure::maxHeightDifference),
        ExtraCodecs.intRange(1, 12).fieldOf("max_chunk_search_distance").forGetter(BridgeStructure::maxChunkSearchDistance),
        Codec.DOUBLE.fieldOf("min_erosion").orElse(Double.NEGATIVE_INFINITY).forGetter(BridgeStructure::minErosion),
        Codec.DOUBLE.fieldOf("max_erosion").orElse(Double.POSITIVE_INFINITY).forGetter(BridgeStructure::maxErosion),
        SimpleWeightedRandomList.wrappedCodec(TemplateData.CODEC).fieldOf("template_data_entries").forGetter(BridgeStructure::templateDataEntries),
        ExtensionData.CODEC.listOf().fieldOf("extensions").forGetter(BridgeStructure::extensions)
    ).apply(instance, BridgeStructure::new));
    private final InclusiveRange<Integer> height;
    private final Integer maxHeightDifference;
    private final Integer maxChunkSearchDistance;
    private final Double minErosion;
    private final Double maxErosion;
    private final SimpleWeightedRandomList<TemplateData> templateDataEntries;
    private final List<ExtensionData> extensions;
    protected BridgeStructure(StructureSettings config, InclusiveRange<Integer> height, Integer maxHeightDifference, Integer maxChunkSearchDistance, Double minErosion, Double maxErosion, SimpleWeightedRandomList<TemplateData> templateDataEntries, List<ExtensionData> extensions) {
        super(config);
        this.height = height;
        this.maxHeightDifference = maxHeightDifference;
        this.maxChunkSearchDistance = maxChunkSearchDistance;
        this.minErosion = minErosion;
        this.maxErosion = maxErosion;
        this.templateDataEntries = templateDataEntries;
        this.extensions = extensions;
    }

    public InclusiveRange<Integer> height() {
        return this.height;
    }

    public Integer maxHeightDifference() {
        return this.maxHeightDifference;
    }

    public Integer maxChunkSearchDistance() {
        return this.maxChunkSearchDistance;
    }

    public Double minErosion() {
        return this.minErosion;
    }

    public Double maxErosion() {
        return this.maxErosion;
    }

    public SimpleWeightedRandomList<TemplateData> templateDataEntries() {
        return this.templateDataEntries;
    }

    public List<ExtensionData> extensions() {
        return this.extensions;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        BridgeData bridgeData = getBridgeData(context);
        if (bridgeData == null) return Optional.empty();

        BlockPos pos = new BlockPos(context.chunkPos().getMiddleBlockX(), bridgeData.getHeight(), context.chunkPos().getMiddleBlockZ());
        var nearbyBiomes = context.biomeSource().getBiomesWithin(pos.getX(), pos.getY(), pos.getZ(), this.maxChunkSearchDistance*4, context.randomState().sampler());
        Optional<TemplateData> templateData = getRandomTemplateData(nearbyBiomes, context.random());
        return templateData.flatMap(data -> onTopOfChunkCenter(context, Heightmap.Types.OCEAN_FLOOR_WG, (collector) -> addPieces(collector, context, pos, bridgeData, data)));
    }
    private void addPieces(StructurePiecesBuilder collector, GenerationContext context, BlockPos pos, BridgeData bridgeData, TemplateData templateData) {
        BridgePieces.addPieces(context.structureTemplateManager(), pos, collector, bridgeData, templateData);
    }

    private BridgeData getBridgeData(GenerationContext context) {
        if (ConfigHandler.getConfig().frequency() != 1.0F && context.random().nextFloat() > ConfigHandler.getConfig().frequency()) return null;
        BlockPos pos = new BlockPos(context.chunkPos().getMiddleBlockX(), 90, context.chunkPos().getMiddleBlockZ());
        double erosion = context.randomState().router().erosion().compute(new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ()));
        if (erosion < minErosion || erosion > maxErosion || getHeightmap(pos, context) > 60) return null;

        var xBridgeData = findValidSegmentLayout(context, pos, Direction.EAST);
        return xBridgeData != null ? xBridgeData : findValidSegmentLayout(context, pos, Direction.SOUTH);
    }

    private BridgeData findValidSegmentLayout(GenerationContext context, BlockPos pos, Direction direction) {
        List<Integer> leftHeights = buildHeightmapList(context, pos, direction.getOpposite());
        List<Integer> rightHeights = buildHeightmapList(context, pos, direction);
        if (leftHeights == null || rightHeights == null) return null;

        Integer negativeHeight = null;
        Integer positiveHeight = null;
        Integer chunkOffset = null;
        Integer totalSegments = null;


        search:
        for (int k = 0; k < leftHeights.size(); k++) {
            Integer leftHeight = leftHeights.get(k);
            if (leftHeight == Integer.MIN_VALUE) continue;
            for (int l = 0; l < rightHeights.size(); l++) {
                Integer rightHeight = rightHeights.get(l);
                if (rightHeight == Integer.MIN_VALUE) continue;
                if (Math.abs(leftHeight - rightHeight) <= this.maxHeightDifference) {
                    negativeHeight = leftHeight;
                    positiveHeight = rightHeight;
                    chunkOffset = -k-1;
                    totalSegments = Math.abs(chunkOffset)+l+1;
                    break search;

                }
            }
        }

        if (negativeHeight == null) return null;

        return new BridgeData(negativeHeight, positiveHeight, chunkOffset, totalSegments, direction);
    }

    private List<Integer> buildHeightmapList(GenerationContext context, BlockPos pos, Direction direction) {
        List<Integer> heights = new ArrayList<>();
        for(int j = 1; j <= this.maxChunkSearchDistance; j++) {
            int height = getHeightmap(pos.relative(direction, 16*j), context);
            if (j != 1 && heights.get(heights.size()-1) > height) {
                break;
            }
            if (this.height.isValueInRange(height)) {
                heights.add(height);
            } else {
                heights.add(Integer.MIN_VALUE);
            }
        }
        return (heights.stream().noneMatch(value -> value != Integer.MIN_VALUE)) ? null : heights;
    }

    private Optional<TemplateData> getRandomTemplateData(Set<Holder<Biome>> nearbyBiomes, RandomSource random) {
        var validEntries = SimpleWeightedRandomList.create(this.templateDataEntries.unwrap().stream().filter(entry -> entry.getData().isValid(nearbyBiomes)).toList());
        Optional<WeightedEntry.Wrapper<TemplateData>> templateCandidate = validEntries.getRandom(random);
        return templateCandidate.map(WeightedEntry.Wrapper::getData);
    }


    private static int getHeightmap(BlockPos pos, GenerationContext context) {
        return context.chunkGenerator().getFirstFreeHeight(pos.getX(), pos.getZ(), Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
    }

    @Override
    public void afterPlace(WorldGenLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox box, ChunkPos chunkPos, PiecesContainer pieces) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int i = world.getMinBuildHeight();
        BoundingBox blockBox = pieces.calculateBoundingBox();
        int j = blockBox.minY();

        for(ExtensionData extension : this.extensions) {
            for(int k = box.minX(); k <= box.maxX(); ++k) {
                for(int l = box.minZ(); l <= box.maxZ(); ++l) {
                    mutable.set(k, j, l);
                    if (world.getBlockState(mutable).is(extension.blocks())) {
                        if (!world.isEmptyBlock(mutable) && blockBox.isInside(mutable) && pieces.isInsidePiece(mutable)) {
                            for(int m = j - 1; m > i; --m) {
                                mutable.setY(m);
                                if (!world.isEmptyBlock(mutable) && !world.getBlockState(mutable).liquid() && !world.getBlockState(mutable).is(Blocks.GRASS_BLOCK)) {
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

    @Override
    public StructureType<?> type() {
        return AbridgedRegistries.BRIDGE_STRUCTURE;
    }

    public record BridgeData(Integer leftHeight, Integer rightHeight, Integer chunkOffset, Integer totalSegments, Direction direction) {
        public Rotation getRotation() {
            return this.direction.getAxis() == Direction.Axis.X ? Rotation.NONE : Rotation.CLOCKWISE_90;
        }
        public Integer getHeight() {
            return Math.min(this.leftHeight, this.rightHeight);
        }
    }

    public record TemplateData(ResourceLocation base, ResourceLocation negativeEdge, ResourceLocation positiveEdge, Holder<StructureProcessorList> processorList, HolderSet<Biome> biomes, Integer offset, List<String> requiredMods) {
        public static final Codec<TemplateData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("base").forGetter(TemplateData::base),
            ResourceLocation.CODEC.fieldOf("negative_edge").forGetter(TemplateData::negativeEdge),
            ResourceLocation.CODEC.fieldOf("positive_edge").forGetter(TemplateData::positiveEdge),
            StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter(TemplateData::processorList),
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(TemplateData::biomes),
            Codec.INT.fieldOf("offset").orElse(0).forGetter(TemplateData::offset),
            Codec.STRING.listOf().fieldOf("required_mods").orElse(List.of()).forGetter(TemplateData::requiredMods)
        ).apply(instance, TemplateData::new));

        public ResourceLocation getProcessorId() {
            Optional<ResourceKey<StructureProcessorList>> key = processorList().unwrapKey();
            return key.map(ResourceKey::location).orElseGet(() -> new ResourceLocation("empty"));
        }

        public boolean isValid(Set<Holder<Biome>> nearbyBiomes) {
            return nearbyBiomes.stream().anyMatch(biomes::contains) && requiredMods.stream().allMatch(Services.PLATFORM::isModLoaded);
        }
    }

    public record ExtensionData(HolderSet<Block> blocks, BlockStateProvider extendedState) {
        public static final Codec<ExtensionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").orElse(HolderSet.direct()).forGetter(ExtensionData::blocks),
            BlockStateProvider.CODEC.fieldOf("extended_state").forGetter(ExtensionData::extendedState)
        ).apply(instance, ExtensionData::new));
    }
}

