package dev.worldgen.abridged.worldgen.stateprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.abridged.registry.AbridgedRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;

public class GradientStateProvider extends BlockStateProvider {
    public static final Codec<GradientStateProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockStateProvider.CODEC.fieldOf("upper").forGetter(GradientStateProvider::upper),
        BlockStateProvider.CODEC.fieldOf("lower").forGetter(GradientStateProvider::lower),
        Codec.INT.fieldOf("start_y").forGetter(GradientStateProvider::startY),
        Codec.INT.fieldOf("end_y").forGetter(GradientStateProvider::endY)
    ).apply(instance, GradientStateProvider::new));

    private final BlockStateProvider upper;
    private final BlockStateProvider lower;
    private final int startY;
    private final int endY;

    public GradientStateProvider(BlockStateProvider upper, BlockStateProvider lower, int startY, int endY) {
        this.upper = upper;
        this.lower = lower;
        this.startY = startY;
        this.endY = endY;
    }

    public BlockStateProvider upper() {
        return this.upper;
    }
    public BlockStateProvider lower() {
        return this.lower;
    }
    public int startY() {
        return this.startY;
    }
    public int endY() {
        return this.endY;
    }

    @Override
    protected BlockStateProviderType<?> type() {
        return AbridgedRegistries.GRADIENT_STATE_PROVIDER;
    }

    @Override
    public BlockState getState(RandomSource random, BlockPos pos) {
        int y = pos.getY();
        if (y > this.endY) return this.upper.getState(random, pos);
        if (y < this.startY) return this.lower.getState(random, pos);
        double d = (double) (y - this.startY) / (this.endY - this.startY);
        return random.nextFloat() < d ? this.upper.getState(random, pos) : this.lower.getState(random, pos);
    }
}
