package dev.worldgen.abridged;

import dev.worldgen.abridged.config.ConfigHandler;
import dev.worldgen.abridged.registry.AbridgedBuiltInRegistries;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class AbridgedFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        AbridgedCommon.init();
        AbridgedBuiltInRegistries.register();
        ConfigHandler.load(FabricLoader.getInstance().getConfigDir().resolve("abridged.json"));
    }
}
