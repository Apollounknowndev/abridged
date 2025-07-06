package dev.worldgen.abridged.registry;

import dev.worldgen.abridged.worldgen.stateprovider.GradientStateProvider;
import dev.worldgen.abridged.worldgen.structure.BridgeStructure;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.msrandom.multiplatform.annotations.Actual;

public class AbridgedRegistriesActual {
    @Actual public static final StructureType<BridgeStructure> BRIDGE_STRUCTURE = () -> BridgeStructure.CODEC.codec();

    @Actual public static final BlockStateProviderType<GradientStateProvider> GRADIENT_STATE_PROVIDER = new BlockStateProviderType<>(GradientStateProvider.CODEC.codec());
}
