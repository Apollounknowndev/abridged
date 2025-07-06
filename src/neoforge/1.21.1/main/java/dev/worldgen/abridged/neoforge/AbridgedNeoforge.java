package dev.worldgen.abridged.neoforge;

import dev.worldgen.abridged.Abridged;
import dev.worldgen.abridged.config.ConfigHandler;
import dev.worldgen.abridged.registry.AbridgedRegistries;
import dev.worldgen.abridged.worldgen.structure.BridgeConfig;
import dev.worldgen.abridged.worldgen.structure.BridgePiece;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Abridged.MOD_ID)
public class AbridgedNeoforge {
    public AbridgedNeoforge(IEventBus bus) {
        ConfigHandler.load(FMLPaths.CONFIGDIR.get());

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
