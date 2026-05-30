package net.countered.terrainslabs.block.interfaces;

import net.countered.terrainslabs.block.OffsetProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public interface IOffsetState {

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean shouldBeOnTopState(BlockGetter level, BlockPos pos, BlockState state) {
        if ( !((IOffsetState) state ).terrain_slabs$hasOffsetState() ) {
            return false;
        }

        // TODO: implement in double plant block
        BlockPos belowPos = pos.below();
        if (state.getBlock() instanceof DoublePlantBlock && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
            belowPos = pos.below(2);
        }

        BlockState belowState = level.getBlockState(belowPos);
        return ISlabCopy.isBottomSlab( belowState ) || ( (IOffsetState) belowState ).terrain_slabs$isOffset();
    }

    boolean terrain_slabs$isOffset();

    boolean terrain_slabs$hasOffsetState();

    EnumProperty<OffsetProperty.OffsetType> terrain_slabs$getOffsetProperty();

    BlockState terrain_slabs$getOppositeState();

    BlockState terrain_slabs$getNormalState();

    BlockState terrain_slabs$getOffsetState();

}
