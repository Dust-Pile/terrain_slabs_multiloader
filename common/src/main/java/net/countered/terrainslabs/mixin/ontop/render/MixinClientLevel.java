package net.countered.terrainslabs.mixin.ontop.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.countered.terrainslabs.block.interfaces.IOffsetState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin( ClientLevel.class )
public class MixinClientLevel {

    @WrapOperation( method = "addDestroyBlockEffect", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleEngine;destroy(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void terrain_slabs$correctParticleState(
            ParticleEngine instance, BlockPos pos, BlockState state, Operation<Void> original
    ) {
        original.call( instance, pos, IOffsetState.getStandardState( state ) );
    }

}
