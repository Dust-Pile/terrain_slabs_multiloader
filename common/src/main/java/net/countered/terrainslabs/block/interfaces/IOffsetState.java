package net.countered.terrainslabs.block.interfaces;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public interface IOffsetState {

    static BlockState getStandardState( BlockState state ) {
        if ( ((IOffsetState) state).terrain_slabs$getOffset() ) {
            return ((IOffsetState) state).terrain_slabs$getOppositeState();
        }

        return state;
    }

    static BlockBehaviour.BlockStateBase getStandardState( BlockBehaviour.BlockStateBase state ) {
        if ( ((IOffsetState) state).terrain_slabs$getOffset() ) {
            return ((IOffsetState) state).terrain_slabs$getOppositeState();
        }

        return state;
    }

    boolean terrain_slabs$getOffset();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean terrain_slabs$hasOffsetState();

    BlockState terrain_slabs$getOppositeState();

    BlockState asState();

}
