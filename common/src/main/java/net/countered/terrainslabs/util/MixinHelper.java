package net.countered.terrainslabs.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.countered.platform.PlatformConfigHooks;
import net.countered.terrainslabs.block.interfaces.IOffsetState;
import net.countered.terrainslabs.block.interfaces.ISlabCopy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Class used to hold methods for near identical mixins to prevent mix-ups.
 */
public class MixinHelper {


    //===============//
    // Proxy Methods //
    //===============//


    public static BlockState terrain_slabs$convertBlockState(
            LevelReader instance, BlockPos offPos, Operation<BlockState> original,
            BlockState state, LevelReader level, BlockPos pos
    ) {
        BlockState stateAtOffset = original.call( instance, offPos );
        if ( skipModify( offPos, state, pos ) || ISlabCopy.notBottomSlab( stateAtOffset ) ) {
            return stateAtOffset;
        }

        return ISlabCopy.getOriginState( stateAtOffset );
    }

    public static boolean terrain_slabs$slabsSupportCenter(
            LevelReader instance, BlockPos offsetPos, Direction direction, Operation<Boolean> original,
            BlockState state, LevelReader level, BlockPos pos
    ) {
        boolean origOutput = original.call( instance, offsetPos, direction );
        if (
                direction != Direction.UP
                        || skipModify( offsetPos, state, pos )
                        || ISlabCopy.notBottomSlab( level.getBlockState( offsetPos ) )
        ) {
            return origOutput;
        }

        return true;
    }

    public static void terrain_slabs$offsetParticles(
            Level instance, ParticleOptions particleData,
            double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed,
            Operation<Void> original,
            BlockState state, Level level, BlockPos pos, RandomSource random
    ) {
        original.call( instance, particleData, x,
                y + state.getOffset( level, pos ).y(),
                z, xSpeed, ySpeed, zSpeed );
    }


    //================//
    // Helper Methods //
    //================//


    private static boolean skipModify( BlockPos offPos, BlockState targetState, BlockPos pos ) {
        return !shouldAllowOffsetState( targetState ) || !((IOffsetState) targetState).terrain_slabs$hasOffsetState()
                || !( offPos.getX() == pos.getX() && offPos.getZ() == pos.getZ() && offPos.getY() == pos.getY() - 1 );
    }

    private static boolean shouldAllowOffsetState( BlockState state ) {
        Block block = state.getBlock();
        if ( isDefaultOffset( block ) ) {
            return !PlatformConfigHooks.excludeOnTop( block );
        }

        return PlatformConfigHooks.includeOntop( block );
    }

    // TODO: Way to dynamically add/remove vegetation classes... (Maybe)
    // hint: someClass.isInstance(someObj)
    private static boolean isDefaultOffset( Block block ) {
        if ( block instanceof BushBlock) {
            return PlatformConfigHooks.isVegetationOnSlabsEnabled();

        } else if ( block instanceof TorchBlock || block instanceof LanternBlock) {
            return true;

        } else if ( block instanceof SnowLayerBlock ) {
            return PlatformConfigHooks.isSnowOnSlabsEnabled();
        }
        return false;
    }
}
