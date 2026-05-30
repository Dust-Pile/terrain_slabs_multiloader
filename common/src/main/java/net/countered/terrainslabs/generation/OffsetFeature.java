package net.countered.terrainslabs.generation;

import com.mojang.serialization.Codec;
import net.countered.platform.PlatformConfigHooks;
import net.countered.terrainslabs.block.interfaces.IOffsetState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Optional;
import java.util.Stack;

public class OffsetFeature extends Feature<NoneFeatureConfiguration> {

    public OffsetFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place( FeaturePlaceContext<NoneFeatureConfiguration> context ) {
        if ( !PlatformConfigHooks.isSlabGenerationEnabled()
                || ( !PlatformConfigHooks.isSnowOnSlabsEnabled() && !PlatformConfigHooks.isVegetationOnSlabsEnabled() )
        ) {
            return false;
        }

        ChunkAccess chunk = context.level().getChunk( context.origin() );
        Optional<Stack<BlockPos>> topSlabs = SlabFeature.TOP_SLAB_CACHE.popChunk( chunk );
        if ( topSlabs.isPresent() ) {
            fixAllOffsetsCached( topSlabs.get(), Direction.UP, context );
        } else {
            fixAllOffsets( context );
        }
        return true;
    }

    private void fixAllOffsetsCached( Stack<BlockPos> stack, Direction dir, FeaturePlaceContext<NoneFeatureConfiguration> context ) {
        if ( !dir.getAxis().isVertical() ) {
            throw new IllegalArgumentException(
                    "Direction inputl to 'fixAllOffsetsCached' in 'OffsetFeature' must have vertical axis"
            );
        }

        WorldGenLevel level = context.level();
        int heightLimit = dir == Direction.UP ? level.getMaxBuildHeight() + 1 : level.getMinBuildHeight() - 1;

        while ( !stack.isEmpty() ) {
            BlockPos pos = stack.pop();
            BlockState state = context.level().getBlockState( pos );

            if ( ( state.getBlock() instanceof SlabBlock ) ) {
                FeatureUtil.iterateInDir( context.level(), pos.relative( dir ), dir, heightLimit,
                        ( aPos, aState ) -> placeOnTopState( level, aPos, aState )
                );
            }
        }
    }

    // This is a fallback method (not preferred)
    private void fixAllOffsets( FeaturePlaceContext<NoneFeatureConfiguration> context ) {
        WorldGenLevel level = context.level();
        FeatureUtil.iterateChunkBlocks( level, context.origin(), ( pos, maxY ) -> {
            BlockState state = level.getBlockState( pos );

            if ( ( state.getBlock() instanceof SlabBlock && state.getValue( SlabBlock.TYPE ) == SlabType.BOTTOM ) ) {
                FeatureUtil.iterateInDir( context.level(), pos.above(), Direction.UP, level.getMaxBuildHeight(),
                        ( aPos, aState ) -> placeOnTopState( level, aPos, aState )
                );
            }
        } );
    }

    private boolean placeOnTopState(WorldGenLevel level, BlockPos pos, BlockState state ) {
        return IOffsetState.shouldBeOnTopState( level, pos, state )
                && level.setBlock( pos, ((IOffsetState) state ).terrain_slabs$getOffsetState(), Block.UPDATE_CLIENTS );
    }
}
