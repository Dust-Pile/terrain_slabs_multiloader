package net.countered.terrainslabs.mixin.ontop.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.countered.terrainslabs.block.interfaces.IOffsetState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin( Level.class )
public class MixinLevel {

    @WrapOperation( method = "destroyBlock", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/gameevent/GameEvent$Context;of(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/gameevent/GameEvent$Context;" ))
    private GameEvent.Context terrain_slabs$stateReplacer(
            Entity sourceEntity, BlockState affectedState, Operation<GameEvent.Context> original
    ) {
        return original.call( sourceEntity, IOffsetState.getStandardState( affectedState ) );
    }

}
