package net.countered.terrainslabs.util;

import net.countered.terrainslabs.block.ModBlockTags;
import net.countered.terrainslabs.platform.PlatformConfigHooks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MixinHelper {

    public static boolean terrain_slabs$isStateValidOnTop(BlockState state) {
        boolean snowCheck = !(!PlatformConfigHooks.isSnowOnSlabsEnabled() && state.is(Blocks.SNOW));
        return (state.is(ModBlockTags.ON_TOP_BLOCKS) || state.getBlock() instanceof VegetationBlock) && snowCheck;
    }
}
