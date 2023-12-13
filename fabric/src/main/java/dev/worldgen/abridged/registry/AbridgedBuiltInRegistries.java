package dev.worldgen.abridged.registry;

import dev.worldgen.abridged.AbridgedCommon;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class AbridgedBuiltInRegistries {
    public static void register() {
        Registry.register(BuiltInRegistries.STRUCTURE_TYPE, new ResourceLocation(AbridgedCommon.MOD_ID, "bridge"), AbridgedRegistries.BRIDGE_STRUCTURE);
        Registry.register(BuiltInRegistries.STRUCTURE_PIECE, new ResourceLocation(AbridgedCommon.MOD_ID, "bridge"), AbridgedRegistries.BRIDGE_PIECE);
        Registry.register(BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE, new ResourceLocation(AbridgedCommon.MOD_ID, "gradient"), AbridgedRegistries.GRADIENT_STATE_PROVIDER);
    }
}
