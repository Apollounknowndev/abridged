package dev.worldgen.abridged.fabric;

import dev.worldgen.abridged.Abridged;
import dev.worldgen.abridged.config.ConfigHandler;
import dev.worldgen.abridged.registry.AbridgedRegistries;
import dev.worldgen.abridged.worldgen.structure.BridgeConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class AbridgedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ConfigHandler.load(FabricLoader.getInstance().getConfigDir());

        DynamicRegistries.register(AbridgedRegistries.BRIDGE_CONFIG_KEY, BridgeConfig.CODEC);

        Registry.register(BuiltInRegistries.STRUCTURE_TYPE, Abridged.id("bridge"), AbridgedRegistries.BRIDGE_STRUCTURE);
        Registry.register(BuiltInRegistries.STRUCTURE_PIECE, Abridged.id("bridge"), AbridgedRegistries.BRIDGE_PIECE);
        Registry.register(BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE, Abridged.id("gradient"), AbridgedRegistries.GRADIENT_STATE_PROVIDER);
    }
}
