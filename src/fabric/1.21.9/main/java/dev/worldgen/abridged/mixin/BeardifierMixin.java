package dev.worldgen.abridged.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.abridged.worldgen.structure.BridgePiece;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(Beardifier.class)
public abstract class BeardifierMixin {
    @Inject(
        method = "forStructuresInChunk",
        at = @At(
            value = "INVOKE",
            target = "add",
            shift = At.Shift.AFTER,
            ordinal = 2
        )
    )
    private static void abridged$setBridgeTerrainAdaption(StructureManager $$0, ChunkPos $$1, CallbackInfoReturnable<Beardifier> cir, @Local StructurePiece structurePiece, @Local(ordinal = 1) List<Beardifier.Rigid> piecesOut) {
        if(structurePiece instanceof BridgePiece bridgePiece && !Objects.equals(bridgePiece.templateName(), BridgePiece.BEARD_BASE.toString())) {
            piecesOut.removeLast();
        }
    }
}