package net.countered.terrainslabs.mixin.ontop.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.countered.terrainslabs.block.interfaces.IOffsetState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin( ServerPlayerGameMode.class )
public class MixinServerPlayerGameMode {

    @WrapOperation( method = "destroyBlock", at = @At( value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;") )
    private BlockState terrain_slabs$replaceCopiedState(ServerLevel instance, BlockPos pos, Operation<BlockState> original) {
        BlockState state = original.call( instance, pos );
        return IOffsetState.getStandardState( state );
    }

}
