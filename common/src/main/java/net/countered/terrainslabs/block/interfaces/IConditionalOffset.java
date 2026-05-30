package net.countered.terrainslabs.block.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Interface for spacial case blocks which have states that should not be offset
 * Mixin and implement as needed
 */
public interface IConditionalOffset {

    <L extends LevelHeightAccessor> boolean couldPlaceOnTop( L level, BlockPos pos, BlockState state );

}
