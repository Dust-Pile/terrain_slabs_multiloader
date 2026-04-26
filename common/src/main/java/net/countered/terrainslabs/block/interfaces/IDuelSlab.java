package net.countered.terrainslabs.block.interfaces;

import net.minecraft.world.level.block.Block;

public interface IDuelSlab extends ISlabCopy {

    ISlabCopy getDuel();

    default Block getDuelBlock() {
        return getDuel().getOriginBlock();
    }

}
