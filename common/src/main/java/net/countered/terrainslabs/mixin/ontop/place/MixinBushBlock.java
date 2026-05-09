package net.countered.terrainslabs.mixin.ontop.place;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.countered.platform.PlatformConfigHooks;
import net.countered.terrainslabs.block.interfaces.ISlabCopy;
import net.countered.terrainslabs.util.MixinHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin( targets = {
        "net.minecraft.world.level.block.BushBlock"
})
public class MixinBushBlock {

    // TODO: Add tag for can be placed on waterlogged
    @WrapOperation( method = "canSurvive", at = @At( value = "INVOKE",
            target = "Lnet/minecraft/world/level/LevelReader;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
    ) )
    private BlockState terrain_slabs$convertBlockState(
            LevelReader instance, BlockPos offPos, Operation<BlockState> original,
            BlockState state, LevelReader level, BlockPos pos
    ) {
        BlockState stateAtOffset = original.call( instance, offPos );
        if (
                !PlatformConfigHooks.isVegetationOnSlabsEnabled()
                || !MixinHelper.terrain_slabs$isStateValidOnTop( state )
                || !( offPos.getX() == pos.getX() && offPos.getZ() == pos.getZ() && offPos.getY() == pos.getY() - 1 )
        ) {
            return stateAtOffset;
        }

        if ( stateAtOffset.getBlock() instanceof ISlabCopy ) {
            return ISlabCopy.getOriginState( stateAtOffset );
        }

        return stateAtOffset;
    }

    /**
     * Allows placement of most vegetation on slabs for player and worldgen
     */
//    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
//    private void terrain_slabs$mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
//        if (state.is(ModBlockTags.DIRT_SLABS) &&
//                !state.getValue(BlockStateProperties.WATERLOGGED) &&
//                PlatformConfigHooks.isVegetationOnSlabsEnabled())
//        {
//            cir.setReturnValue(true);
//        }
//    }
}
