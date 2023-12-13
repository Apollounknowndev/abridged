package dev.worldgen.abridged;

import dev.worldgen.abridged.registry.AbridgedBuiltInRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AbridgedCommon.MOD_ID)
public class AbridgedForge {
    public AbridgedForge() {
        AbridgedCommon.init();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        AbridgedBuiltInRegistries.register(bus);
    }
}