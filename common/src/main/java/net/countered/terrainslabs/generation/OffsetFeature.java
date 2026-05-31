package net.countered.terrainslabs.generation;

import com.mojang.serialization.Codec;
import net.countered.platform.PlatformConfigHooks;
import net.countered.terrainslabs.block.interfaces.IOffsetState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class OffsetFeature extends Feature<NoneFeatureConfiguration> {

    protected static final FeatureUtil.BlockPosCache BOTTOM_SLAB_CACHE = new FeatureUtil.BlockPosCache(
            ( cache, chunk ) -> {
                fixChunkOffsetsCached( cache.getLevel( chunk ), chunk.getPos().getWorldPosition(), cache, Direction.UP );
            } );

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

        LevelAccessor level = context.level();
        BlockPos origin = context.origin();
        ChunkAccess chunk = level.getChunk( origin );
        if ( BOTTOM_SLAB_CACHE.containsChunk( chunk ) ) {
            fixChunkOffsetsCached( level, origin, BOTTOM_SLAB_CACHE, Direction.UP );
        } else {
            fixChunkOffsets( context );
        }
        fixSurfaceOffsets( Heightmap.Types.MOTION_BLOCKING, context );

        BOTTOM_SLAB_CACHE.getNeighbors( chunk ).forEach( BOTTOM_SLAB_CACHE::tryTrimChunks );
        return true;
    }

    private static void fixChunkOffsetsCached( LevelAccessor level, BlockPos origin, FeatureUtil.BlockPosCache cache, Direction dir) {
        if ( !dir.getAxis().isVertical() ) {
            throw new IllegalArgumentException(
                    "Direction input to 'fixAllOffsetsCached' in 'OffsetFeature' must have vertical axis"
            );
        }

        ChunkAccess chunk = level.getChunk( origin );
        cache.forExistingQueue( chunk, ( pos ) -> {
            if ( !replaceStatesInDir( level, pos, dir ) ) {
                cache.addBlockPos( level, pos );
            }
        } );
    }

    // This is a fallback method (not preferred)
    private static void fixChunkOffsets(FeaturePlaceContext<NoneFeatureConfiguration> context ) {
        WorldGenLevel level = context.level();
        FeatureUtil.forEachChunkBlock( level, context.origin(), Heightmap.Types.WORLD_SURFACE_WG, (pos, maxY ) -> {
            BlockState state = level.getBlockState( pos );

            if ( !( state.getBlock() instanceof SlabBlock ) ) {
                return; //continue
            }

            if ( ( state.getValue( SlabBlock.TYPE ) == SlabType.BOTTOM )
                    && !replaceStatesInDir( level, pos, Direction.UP )
            ) {
                // We're here, may as well populate the cache.
                BOTTOM_SLAB_CACHE.addBlockPos( level, pos );
            }
        } );

        // Ensure cache for chunk exists
        BOTTOM_SLAB_CACHE.mapChunk( level, level.getChunk( context.origin() ) );
    }

    private static void fixSurfaceOffsets( Heightmap.Types heightType, FeaturePlaceContext<NoneFeatureConfiguration> context ) {
        WorldGenLevel level = context.level();
        FeatureUtil.forEachSurfaceBlock( level, context.origin(), heightType, (topPos, minY ) -> {
            replaceStatesInDir( level, topPos, Direction.UP );
        } );
    }

    private static boolean replaceStatesInDir( LevelAccessor level, BlockPos pos, Direction dir ) {
        return FeatureUtil.iterateDirUntilFail( level, pos.relative( dir ), dir,
                ( aPos, aState ) -> placeOntopState( level, aPos, aState )
        );
    }

    // TODO: Rename and handle onbottom states
    private static boolean placeOntopState( LevelAccessor level, BlockPos pos, BlockState state ) {
        return IOffsetState.shouldBeOnTopState( level, pos, state )
                && level.setBlock( pos, ((IOffsetState) state ).terrain_slabs$getOffsetState(), Block.UPDATE_CLIENTS );
    }
}
