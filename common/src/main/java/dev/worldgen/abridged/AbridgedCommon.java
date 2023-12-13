package dev.worldgen.abridged;

import dev.worldgen.abridged.registry.AbridgedRegistries;

public class AbridgedCommon {
    public static final String MOD_ID = "abridged";
    public static void init() {
        AbridgedRegistries.init();
    }
}