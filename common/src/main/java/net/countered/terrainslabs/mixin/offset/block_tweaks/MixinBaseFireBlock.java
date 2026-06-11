package net.countered.terrainslabs.mixin.offset.block_tweaks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.countered.terrainslabs.api.IConditionalOffset;
import net.countered.terrainslabs.block.interfaces.ISlabCopy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin( BaseFireBlock.class )
public class MixinBaseFireBlock implements IConditionalOffset {

    @Override
    @Unique
    public <L extends LevelHeightAccessor> boolean terrain_slabs$couldPlaceOnTop(L level, BlockPos pos, BlockState state) {
        return state.is( Blocks.SOUL_FIRE ) || terrain_slabs$isBottomSupportedFire( (BlockGetter) level, pos, state );
    }

    /**
     * When calling for the state below a block, pretends it's the matching full block when relevant.
     */
    // TODO: make config for disabling fire offset
    @WrapOperation( method = "getState", at = @At( value = "INVOKE",
            target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
    ) )
    private static BlockState terrain_slabs$convertBlockState(
            BlockGetter instance, BlockPos offPos, Operation<BlockState> original,
            BlockGetter reader, BlockPos pos
    ) {
        BlockState stateAtOffset = original.call( instance, offPos );
        if ( ISlabCopy.notBottomSlab( stateAtOffset ) ) {
            return stateAtOffset;
        }

        return ISlabCopy.getOriginState( stateAtOffset );
    }

    private static boolean terrain_slabs$isBottomSupportedFire( BlockGetter level, BlockPos pos, BlockState state ) {
        return false;
    }
}
