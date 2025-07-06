package dev.worldgen.abridged;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.msrandom.multiplatform.annotations.Actual;

import java.util.Optional;

public class AbridgedActual {
    @Actual
    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(Abridged.MOD_ID, name);
    }

    @Actual
    public static String getString(CompoundTag tag, String name) {
        return tag.getStringOr(name, "unknown");
    }

    @Actual
    public static int getInt(CompoundTag tag, String name) {
        return tag.getIntOr(name, 0);
    }

    @Actual
    public static Optional<StructureProcessorList> getProcessor(RegistryAccess registries, ResourceLocation id) {
        return registries.lookupOrThrow(Registries.PROCESSOR_LIST).getOptional(id);
    }
}
