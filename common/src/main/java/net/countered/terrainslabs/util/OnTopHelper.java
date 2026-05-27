package net.countered.terrainslabs.util;

import net.countered.terrainslabs.registries.ModOnTopBlocksRegistry;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;

public class OnTopHelper {

    public static boolean terrain_slabs$isStateValidOnTop(BlockState state) {
        return ModOnTopBlocksRegistry.getOnTopBlocks().contains(state.getBlock())
                || state.getBlock() instanceof VegetationBlock;
    }
}
