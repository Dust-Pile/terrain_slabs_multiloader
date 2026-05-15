package net.countered.terrainslabs.mixin.ontop.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.countered.terrainslabs.TerrainSlabs;
import net.countered.terrainslabs.block.interfaces.IOffsetState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin( LevelRenderer.class )
public class MixinLevelRenderer {

    @Shadow
    private ClientLevel level;

    /**
     * Position particles correctly on block break. Not critical, just nice.
     */
    @WrapOperation( method = "levelEvent", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;stateById(I)Lnet/minecraft/world/level/block/state/BlockState;" ) )
    private BlockState terrain_slabs$eventStateFixer(
            int id, Operation<BlockState> original,
            int type, BlockPos pos, int data
    ) {
        BlockState state = original.call( id );
        if ( ((IOffsetState) state ).terrain_slabs$hasOffsetState() ) {
            return this.level.getBlockState( pos );
        }

        return state;
    }

}
