package dev.worldgen.abridged.registry;

import dev.worldgen.abridged.worldgen.stateprovider.GradientStateProvider;
import dev.worldgen.abridged.worldgen.structure.BridgePieces;
import dev.worldgen.abridged.worldgen.structure.BridgeStructure;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class AbridgedRegistries {
    public static final StructureType<BridgeStructure> BRIDGE_STRUCTURE = () -> BridgeStructure.CODEC;
    public static final StructurePieceType BRIDGE_PIECE = (StructurePieceType.StructureTemplateType) BridgePieces.Piece::new;
    public static final BlockStateProviderType<GradientStateProvider> GRADIENT_STATE_PROVIDER = new BlockStateProviderType<>(GradientStateProvider.CODEC);
    public static void init() {}
}
