package dev.worldgen.abridged.worldgen.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.placementcondition.PlacementCondition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.List;
import java.util.Optional;

public record BridgeConfig(PlacementCondition condition, InclusiveRange<Integer> height, InclusiveRange<Integer> segments, Integer maxHeightDifference, List<ResourceLocation> base, List<ResourceLocation> edge, Holder<StructureProcessorList> processors, List<Extension> extensions) {
    public static final Codec<List<ResourceLocation>> COMPACT_ID_LIST_CODEC = compactList(ResourceLocation.CODEC);

    public static final Codec<List<Block>> COMPACT_BLOCK_LIST_CODEC = compactList(BuiltInRegistries.BLOCK.byNameCodec());

    public static final Codec<BridgeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        PlacementCondition.CODEC.fieldOf("condition").forGetter(BridgeConfig::condition),
        InclusiveRange.codec(Codec.INT).fieldOf("height").forGetter(BridgeConfig::height),
        InclusiveRange.codec(Codec.intRange(2, 12)).fieldOf("segments").forGetter(BridgeConfig::segments),
        Codec.INT.fieldOf("max_height_difference").forGetter(BridgeConfig::maxHeightDifference),
        COMPACT_ID_LIST_CODEC.fieldOf("base_template").forGetter(BridgeConfig::base),
        COMPACT_ID_LIST_CODEC.fieldOf("edge_template").forGetter(BridgeConfig::edge),
        StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter(BridgeConfig::processors),
        Extension.CODEC.listOf().fieldOf("extensions").forGetter(BridgeConfig::extensions)
    ).apply(instance, BridgeConfig::new));

    public ResourceLocation getProcessorId() {
        Optional<ResourceKey<StructureProcessorList>> key = processors.unwrapKey();
        return key.map(ResourceKey::location).orElseGet(() -> ResourceLocation.tryParse("minecraft:empty"));
    }

    public record Extension(List<Block> blocks, BlockStateProvider extendedState) {
        public static final Codec<Extension> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            COMPACT_BLOCK_LIST_CODEC.fieldOf("blocks").forGetter(Extension::blocks),
            BlockStateProvider.CODEC.fieldOf("extended_state").forGetter(Extension::extendedState)
        ).apply(instance, Extension::new));
    }

    private static <T> Codec<List<T>> compactList(Codec<T> codec) {
        return Codec.either(
            codec.listOf(), codec
        ).xmap(
            either -> either.map(id -> id, List::of),
            Either::left
        );
    }
}
