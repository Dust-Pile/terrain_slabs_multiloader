package net.countered.terrainslabs.mixin.offset.state;

import net.countered.terrainslabs.block.OffsetProperty;
import net.countered.terrainslabs.mixin_applier.ClassCacheAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mixin( StateDefinition.class )
public abstract class MixinStateDefinition {

    private static List<Class<?>> excludedClasses = new ArrayList<>( List.of(
            LiquidBlock.class, NoteBlock.class, EntityBlock.class, // Maybe not this one...
            BaseRailBlock.class, PistonHeadBlock.class, PistonBaseBlock.class,
            RedStoneWireBlock.class, BarrierBlock.class, LightBlock.class,
            CarpetBlock.class, WallBannerBlock.class, SlabBlock.class,
            EndGatewayBlock.class, GameMasterBlock.class, StructureVoidBlock.class,
            AirBlock.class, WallBlock.class, BasePressurePlateBlock.class,
            CrossCollisionBlock.class, FenceGateBlock.class,
            StairBlock.class, WallSignBlock.class, ButtonBlock.class
    ) );

    /**
     * All blocks are baked with offset states, we will check whether to use them later.
     */
    @ModifyVariable( method = "<init>", at = @At("HEAD"), argsOnly = true )
    private static <O, S> Map<String, Property<?>> terrain_slabs$addOffsetProperty(
            Map<String, Property<?>> propertiesByName,
            Function<O, S> stateValueFunction, O owner, StateDefinition.Factory<O, S> valueFunction
    ) {
        if ( !( owner instanceof Block ) || terrain_slabs$instanceofExcludedClass( ( (Block) owner ) ) ) {
            return propertiesByName;
        }

        propertiesByName.put( "offset", OffsetProperty.ONTOP );
        ClassCacheAccess.addToCache( owner.getClass().getName() );
        return propertiesByName;
    }


    //=========//
    // Helpers //
    //=========//


    @Unique
    private static boolean terrain_slabs$instanceofExcludedClass(Block block ) {
        for ( Class<?> clazz : excludedClasses ) {
            if ( clazz.isInstance( block ) ) {
                return true;
            }
        }

        return false;
    }
}
