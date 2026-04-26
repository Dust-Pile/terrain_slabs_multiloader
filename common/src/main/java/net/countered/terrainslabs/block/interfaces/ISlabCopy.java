package net.countered.terrainslabs.block.interfaces;

import net.minecraft.world.level.block.Block;

public interface ISlabCopy {

    Block getOriginBlock();

    default Block getBlock() {
        return ( Block ) this;
    }

}
