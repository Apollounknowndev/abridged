package dev.worldgen.abridged;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.msrandom.multiplatform.annotations.Expect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Abridged {
    public static final String MOD_ID = "abridged";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Various multi-version utils

    @Expect public static ResourceLocation id(String name);

    @Expect public static String getString(CompoundTag tag, String name);

    @Expect public static int getInt(CompoundTag tag, String name);

    @Expect public static Optional<StructureProcessorList> getProcessor(RegistryAccess registries, ResourceLocation id);
}
