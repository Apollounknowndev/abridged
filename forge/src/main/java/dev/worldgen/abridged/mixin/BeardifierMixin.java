package dev.worldgen.abridged.mixin;

import dev.worldgen.abridged.worldgen.structure.BridgePieces;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;

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

@Mixin(Beardifier.class)
public abstract class BeardifierMixin {
    @Inject(
        method = "lambda$forStructuresInChunk$2(Lnet/minecraft/world/level/ChunkPos;Lit/unimi/dsi/fastutil/objects/ObjectList;IILit/unimi/dsi/fastutil/objects/ObjectList;Lnet/minecraft/world/level/levelgen/structure/StructureStart;)V",
        at = @At(
            value = "INVOKE",
            target = "Lit/unimi/dsi/fastutil/objects/ObjectList;add(Ljava/lang/Object;)Z",
            shift = At.Shift.AFTER,
            ordinal = 3
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void abridged$setBridgeTerrainAdaption(ChunkPos pos, ObjectList<Beardifier.Rigid> piecesOut, int startX, int startZ, ObjectList<JigsawJunction> jigsawJunctionsOut, StructureStart start, CallbackInfo ci, TerrainAdjustment structureTerrainAdaptation, Iterator<JigsawJunction> var7, StructurePiece structurePiece) {
        if(structurePiece instanceof BridgePieces.Piece bridgePiece && !Objects.equals(bridgePiece.id, BridgePieces.BEARD_BASE.toString())) {
            piecesOut.remove(piecesOut.size()-1);
        }
    }
}