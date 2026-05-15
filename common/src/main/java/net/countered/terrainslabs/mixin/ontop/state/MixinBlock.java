package net.countered.terrainslabs.mixin.ontop.state;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.countered.terrainslabs.block.interfaces.IOffsetState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin( Block.class )
public class MixinBlock {

    /**
     * Get ID for the state that is actually registered... (otherwise just "0" for air)
     */
    @WrapOperation( method = "spawnDestroyParticles", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getId(Lnet/minecraft/world/level/block/state/BlockState;)I" ) )
    private int terrain_slabs$getCorrectId( BlockState state, Operation<Integer> original ) {
        return original.call( IOffsetState.getStandardState( state ) );
    }

}
