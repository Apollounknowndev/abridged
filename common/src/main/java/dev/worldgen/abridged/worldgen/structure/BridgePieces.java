package dev.worldgen.abridged.worldgen.structure;

import dev.worldgen.abridged.AbridgedCommon;
import dev.worldgen.abridged.registry.AbridgedRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class BridgePieces {
    public BridgePieces() {}

    public static final ResourceLocation BEARD_BASE = new ResourceLocation(AbridgedCommon.MOD_ID, "beard_base");

    public static void addPieces(StructureTemplateManager manager, BlockPos pos, StructurePieceAccessor holder, BridgeStructure.BridgeData bridgeData, BridgeStructure.TemplateData templateData) {
        for (int i = 0; i < bridgeData.totalSegments(); i++) {
            ResourceLocation id = templateData.base();
            if (i == 0) {
                id = templateData.negativeEdge();
            } else if (i == bridgeData.totalSegments() - 1) {
                id = templateData.positiveEdge();
            }
            holder.addPiece(new Piece(manager, id, pos.relative(bridgeData.direction(), (bridgeData.chunkOffset()+i)*16), bridgeData.getRotation(), templateData.offset(), templateData.getProcessorId()));
        }
        holder.addPiece(new Piece(manager, BEARD_BASE, pos.relative(bridgeData.direction(), bridgeData.chunkOffset()*16).relative(Direction.DOWN, templateData.offset()), bridgeData.getRotation(), templateData.offset(), templateData.getProcessorId()));
        holder.addPiece(new Piece(manager, BEARD_BASE, pos.relative(bridgeData.direction(), (bridgeData.chunkOffset()+bridgeData.totalSegments())*16-8).relative(Direction.DOWN, templateData.offset()), bridgeData.getRotation(), templateData.offset(), templateData.getProcessorId()));
    }
    public static class Piece extends TemplateStructurePiece {
        public String id;
        public Integer offset;
        public ResourceLocation processorId;

        public Piece(StructureTemplateManager manager, ResourceLocation identifier, BlockPos pos, Rotation blockRotation, Integer offset, ResourceLocation processorId) {
            super(AbridgedRegistries.BRIDGE_PIECE, 0, manager, identifier, identifier.toString(), new StructurePlaceSettings().setRotation(blockRotation), pos);
            this.id = identifier.toString();
            this.offset = offset;
            this.processorId = processorId;
        }

        public Piece(StructureTemplateManager manager, CompoundTag nbt) {
            super(AbridgedRegistries.BRIDGE_PIECE, nbt, manager, (id) -> new StructurePlaceSettings().setRotation(Rotation.valueOf(nbt.getString("rotation"))));
            this.id = nbt.getString("Template");
            this.offset = nbt.getInt("offset");
            this.processorId = new ResourceLocation(nbt.getString("processor"));
        }
        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
            super.addAdditionalSaveData(context, nbt);
            nbt.putInt("offset", this.offset);
            nbt.putString("rotation", this.placeSettings.getRotation().name());
            nbt.putString("processor", this.processorId.toString());
        }

        @Override
        public void postProcess(WorldGenLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
            StructureProcessorList processorList = structureAccessor.registryAccess().registryOrThrow(Registries.PROCESSOR_LIST).get(processorId);
            if (processorList != null) {
                for (StructureProcessor processor : processorList.list()) {
                    this.placeSettings.addProcessor(processor);
                }
            }
            super.postProcess(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
        }

        @Override
        protected void handleDataMarker(String metadata, BlockPos pos, ServerLevelAccessor world, RandomSource random, BoundingBox boundingBox) {}
    }
}