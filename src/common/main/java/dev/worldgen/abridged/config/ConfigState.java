package dev.worldgen.abridged.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ConfigState {
    public static final Codec<ConfigState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.floatRange(0.0f, 1.0f).fieldOf("frequency").orElse(0.8f).forGetter(state -> state.frequency),
        Codec.BOOL.fieldOf("directly_sample_heightmap").orElse(false).forGetter(state -> state.directlySampleHeightmap)
    ).apply(instance, ConfigState::new));

    public ConfigState(float frequency, boolean directlySampleHeightmap) {
        this.frequency = frequency;
        this.directlySampleHeightmap = directlySampleHeightmap;
    }

    /**
     * The frequency of valid bridge configurations actually placing.
     */
    public float frequency;
    /**
     * Directly samples the heightmap when checking for a valid bridge configuration.
     * <p>
     * By default, this is disabled and an approximation of the heightmap is used with the {@code depth} field in the noise router.
     * <p>
     * The approximation is vastly more efficient and increases successful bridge placements, but may break under strange circumstances (e.g. Amplified).
     */
    public boolean directlySampleHeightmap;
}
