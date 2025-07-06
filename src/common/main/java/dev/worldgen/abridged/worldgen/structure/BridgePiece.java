package dev.worldgen.abridged.worldgen.structure;

import dev.worldgen.abridged.Abridged;
import dev.worldgen.abridged.registry.AbridgedRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.Optional;

public class BridgePiece extends TemplateStructurePiece {
    public static final ResourceLocation BEARD_BASE = Abridged.id("beard_base");
    public final ResourceLocation configId;
    public final ResourceLocation processorId;

    public BridgePiece(StructureTemplateManager manager, ResourceLocation id, BlockPos pos, Rotation rotation, Holder<BridgeConfig> config) {
        this(manager, id, pos, rotation, Mirror.NONE, config);
    }

    public BridgePiece(StructureTemplateManager manager, ResourceLocation id, BlockPos pos, Rotation rotation, Mirror mirror, Holder<BridgeConfig> config) {
        super(AbridgedRegistries.BRIDGE_PIECE, 0, manager, id, id.toString(), new StructurePlaceSettings().setRotation(rotation).setMirror(mirror), pos);
        this.configId = config.unwrapKey().get().location();
        this.processorId = config.value().getProcessorId();
    }

    public BridgePiece(StructureTemplateManager manager, CompoundTag nbt) {
        super(AbridgedRegistries.BRIDGE_PIECE, nbt, manager, id -> new StructurePlaceSettings().setRotation(Rotation.valueOf(Abridged.getString(nbt, "rotation"))).setMirror(Mirror.valueOf(Abridged.getString(nbt, "mirror"))));
        this.configId = ResourceLocation.tryParse(Abridged.getString(nbt, "config"));
        this.processorId = ResourceLocation.tryParse(Abridged.getString(nbt, "processor"));
    }

    public String templateName() {
        return this.templateName;
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        super.addAdditionalSaveData(context, nbt);
        nbt.putString("rotation", this.placeSettings.getRotation().name());
        nbt.putString("mirror", this.placeSettings.getMirror().name());
        nbt.putString("config", this.configId.toString());
        nbt.putString("processor", this.processorId.toString());
    }

    @Override
    public void postProcess(WorldGenLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        Optional<StructureProcessorList> processorList = Abridged.getProcessor(structureAccessor.registryAccess(), processorId);
        if (processorList.isPresent()) {
            for (StructureProcessor processor : processorList.get().list()) {
                this.placeSettings.addProcessor(processor);
            }
        }
        this.placeSettings.addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
        super.postProcess(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
    }

    @Override
    protected void handleDataMarker(String metadata, BlockPos pos, ServerLevelAccessor levelAccessor, RandomSource random, BoundingBox boundingBox) {}
}