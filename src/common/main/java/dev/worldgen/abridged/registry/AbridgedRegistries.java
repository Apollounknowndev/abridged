package dev.worldgen.abridged.registry;

import dev.worldgen.abridged.Abridged;
import dev.worldgen.abridged.worldgen.stateprovider.GradientStateProvider;
import dev.worldgen.abridged.worldgen.structure.BridgeConfig;
import dev.worldgen.abridged.worldgen.structure.BridgePiece;
import dev.worldgen.abridged.worldgen.structure.BridgeStructure;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.msrandom.multiplatform.annotations.Expect;

public class AbridgedRegistries {
    public static final StructurePieceType BRIDGE_PIECE = (StructurePieceType.StructureTemplateType) BridgePiece::new;

    public static final ResourceKey<Registry<BridgeConfig>> BRIDGE_CONFIG_KEY = ResourceKey.createRegistryKey(Abridged.id("bridge_config"));

    @Expect public static final StructureType<BridgeStructure> BRIDGE_STRUCTURE;

    @Expect public static final BlockStateProviderType<GradientStateProvider> GRADIENT_STATE_PROVIDER;
}
