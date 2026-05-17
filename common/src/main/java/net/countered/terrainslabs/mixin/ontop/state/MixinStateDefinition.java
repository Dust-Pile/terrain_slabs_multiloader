package net.countered.terrainslabs.mixin.ontop.state;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.countered.terrainslabs.block.interfaces.IOffsetState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin( StateDefinition.class )
public abstract class MixinStateDefinition<O, S> {

    /**
     * correctly populate states to fix crash on offset state change
     */
    @WrapOperation( method = "<init>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/StateHolder;populateNeighbours(Ljava/util/Map;)V" ) )
    private void terrain_slabs$populateMirrorNeighbors(
            StateHolder<O, S> instance, Map<Map<Property<?>, Comparable<?>>, S> possibleStateMap,
            Operation<Void> original
    ) {
        original.call( instance, possibleStateMap);
        if ( instance instanceof IOffsetState offsetState && offsetState.terrain_slabs$hasOffsetState() ) {
            original.call( offsetState.terrain_slabs$getOppositeState(), possibleStateMap);
        }
    }

}
