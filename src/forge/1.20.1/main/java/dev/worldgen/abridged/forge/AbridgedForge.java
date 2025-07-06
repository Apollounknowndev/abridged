package dev.worldgen.abridged.forge;

import dev.worldgen.abridged.Abridged;
import dev.worldgen.abridged.config.ConfigHandler;
import dev.worldgen.abridged.registry.AbridgedRegistries;
import dev.worldgen.abridged.worldgen.structure.BridgeConfig;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;

@Mod(Abridged.MOD_ID)
public class AbridgedForge {
    public AbridgedForge() {
        ConfigHandler.load(FMLPaths.CONFIGDIR.get());
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener((RegisterEvent event) -> {
            event.register(Registries.STRUCTURE_TYPE, helper -> {
                helper.register(Abridged.id("bridge"), AbridgedRegistries.BRIDGE_STRUCTURE);
            });
            event.register(Registries.STRUCTURE_PIECE, helper -> {
                helper.register(Abridged.id("bridge"), AbridgedRegistries.BRIDGE_PIECE);
            });
            event.register(Registries.BLOCK_STATE_PROVIDER_TYPE, helper -> {
                helper.register(Abridged.id("gradient"), AbridgedRegistries.GRADIENT_STATE_PROVIDER);
            });
        });

        bus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
            event.dataPackRegistry(AbridgedRegistries.BRIDGE_CONFIG_KEY, BridgeConfig.CODEC);
        });
    }
}
