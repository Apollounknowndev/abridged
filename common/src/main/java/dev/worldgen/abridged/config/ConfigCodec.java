package dev.worldgen.abridged.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public record ConfigCodec(float frequency) {
    public static final Codec<ConfigCodec> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
        Codec.floatRange(0.0F, 1.0F).fieldOf("frequency").forGetter(ConfigCodec::frequency)
    ).apply(instance, ConfigCodec::new));
}