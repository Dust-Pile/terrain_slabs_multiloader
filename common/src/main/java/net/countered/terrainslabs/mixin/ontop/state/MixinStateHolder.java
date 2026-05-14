package net.countered.terrainslabs.mixin.ontop.state;

import net.minecraft.world.level.block.state.StateHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin( StateHolder.class )
public interface MixinStateHolder<O, S> {

    @Accessor( "owner" )
    O terrain_slabs$getOwner();

}
