package net.countered.terrainslabs.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Interface for spacial case blocks which have states that should not be offset
 * Mixin and implement as needed
 */
public interface IConditionalOffset {

    <L extends BlockGetter> boolean terrain_slabs$couldPlaceOntop(L level, BlockPos pos, BlockState state );

    <L extends BlockGetter> boolean terrain_slabs$couldPlaceOnbottom( L level, BlockPos pos, BlockState state );

}
