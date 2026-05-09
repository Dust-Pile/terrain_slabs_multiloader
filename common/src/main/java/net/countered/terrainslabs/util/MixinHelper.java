package net.countered.terrainslabs.util;

import net.countered.platform.PlatformConfigHooks;
import net.countered.terrainslabs.block.ModBlockTags;
import net.countered.terrainslabs.block.interfaces.ISlabCopy;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;

public class MixinHelper {

    public static boolean terrain_slabs$isStateValidOnTop(BlockState state) {
        return state.is(ModBlockTags.ON_TOP_BLOCKS)
                || state.getBlock() instanceof BushBlock
                || state.getBlock() instanceof TorchBlock
                || state.getBlock() instanceof LanternBlock;
    }

    public static boolean terrain_slabs$notBottomSlab( BlockState state ) {
        return !( state.getBlock() instanceof ISlabCopy)
                || state.getValue( SlabBlock.TYPE ) != SlabType.BOTTOM;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean checkOnTopState(BlockGetter level, BlockPos pos, BlockState state ) {
        if ( !MixinHelper.terrain_slabs$isStateValidOnTop(state) ) {
            return false;
        }

        BlockPos belowPos = pos.below();
        if (state.getBlock() instanceof DoublePlantBlock && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
            belowPos = pos.below(2);
        }

        BlockState belowState = level.getBlockState(belowPos);

        if (belowState.is(BlockTags.SLABS)) {
            return belowState.hasProperty(SlabBlock.TYPE) && belowState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM;
        }

        return false;
    }
}
