package dev.worldgen.abridged.mixin;

import dev.worldgen.abridged.worldgen.structure.BridgePieces;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Objects;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

@Mixin(value = Beardifier.class, remap = false)
public abstract class BeardifierMixin {
    @Inject(
        method = "method_42694",
        at = @At(
            value = "INVOKE",
            target = "Lit/unimi/dsi/fastutil/objects/ObjectList;add(Ljava/lang/Object;)Z",
            shift = At.Shift.AFTER,
            ordinal = 2
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void abridged$setBridgeTerrainAdaption(ChunkPos $$0x, ObjectList $$1x, int $$2x, int $$3x, ObjectList $$4x, StructureStart $$5x, CallbackInfo ci, TerrainAdjustment $$6, Iterator var7, StructurePiece $$7) {
        if($$7 instanceof BridgePieces.Piece bridgePiece && !Objects.equals(bridgePiece.id, BridgePieces.BEARD_BASE.toString())) {
            $$1x.remove($$1x.size()-1);
        }
    }
}
