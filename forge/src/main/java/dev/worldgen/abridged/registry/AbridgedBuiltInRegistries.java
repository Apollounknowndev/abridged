package dev.worldgen.abridged.registry;

import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegisterEvent;

public class AbridgedBuiltInRegistries {
    public static void register(IEventBus bus) {
        bus.addListener((RegisterEvent event) -> {
            event.register(Registries.STRUCTURE_TYPE, (helper) -> helper.register("bridge", AbridgedRegistries.BRIDGE_STRUCTURE));
            event.register(Registries.STRUCTURE_PIECE, (helper) -> helper.register("bridge", AbridgedRegistries.BRIDGE_PIECE));
            event.register(Registries.BLOCK_STATE_PROVIDER_TYPE, (helper) -> helper.register("gradient", AbridgedRegistries.GRADIENT_STATE_PROVIDER));
        });
    }
}
