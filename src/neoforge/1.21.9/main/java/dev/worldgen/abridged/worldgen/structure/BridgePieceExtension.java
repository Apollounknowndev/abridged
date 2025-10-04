package dev.worldgen.abridged.worldgen.structure;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.msrandom.classextensions.ClassExtension;
import net.msrandom.classextensions.ExtensionInject;
import net.msrandom.classextensions.ExtensionShadow;
import net.neoforged.neoforge.common.world.PieceBeardifierModifier;

@ClassExtension(BridgePiece.class)
public class BridgePieceExtension implements PieceBeardifierModifier {
    @ExtensionShadow
    protected final String templateName;

    @ExtensionShadow
    protected BoundingBox boundingBox;

    @ExtensionInject
    @Override
    public BoundingBox getBeardifierBox() {
        return boundingBox;
    }

    @ExtensionInject
    @Override
    public TerrainAdjustment getTerrainAdjustment() {
        return this.templateName.equals(BridgePiece.BEARD_BASE.toString()) ? TerrainAdjustment.BEARD_THIN : TerrainAdjustment.NONE;
    }

    @ExtensionInject
    @Override
    public int getGroundLevelDelta() {
        return 0;
    }
}
