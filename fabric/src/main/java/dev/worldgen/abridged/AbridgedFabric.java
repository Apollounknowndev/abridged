package dev.worldgen.abridged;

import dev.worldgen.abridged.registry.AbridgedBuiltInRegistries;
import net.fabricmc.api.ModInitializer;

public class AbridgedFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        AbridgedCommon.init();
        AbridgedBuiltInRegistries.register();
    }
}
