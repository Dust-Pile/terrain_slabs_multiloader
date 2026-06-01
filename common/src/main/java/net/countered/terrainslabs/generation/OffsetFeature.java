package net.countered.terrainslabs.generation;

import net.countered.platform.PlatformConfigHooks;
import net.countered.terrainslabs.TerrainSlabs;
import net.countered.terrainslabs.block.interfaces.IOffsetState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Heightmap;

final public class OffsetFeature {

    public static final WorldGenCache BOTTOM_SLAB_CACHE = new WorldGenCache();

    public static void run( ProtoChunk chunk ) {
        if ( !PlatformConfigHooks.isSlabGenerationEnabled()
                || ( !PlatformConfigHooks.isSnowOnSlabsEnabled() && !PlatformConfigHooks.isVegetationOnSlabsEnabled() )
        ) {
            return;
        }

        if ( !BOTTOM_SLAB_CACHE.containsChunk( chunk ) ) {
            TerrainSlabs.LOGGER.error( "Chunk {} failed to load offsets due to missing cache element.", chunk );
            return;
        } else {
            LevelAccessor level = BOTTOM_SLAB_CACHE.getLevel( chunk );
            fixChunkOffsetsCached( level, chunk, BOTTOM_SLAB_CACHE, Direction.UP );

            // Ensure slabs on structures / other features get proper snow place
            fixSurfaceOffsets( level, chunk, Heightmap.Types.MOTION_BLOCKING );
        }

        BOTTOM_SLAB_CACHE.removeChunk( chunk );
    }

    private static void fixChunkOffsetsCached( LevelAccessor level, ChunkAccess chunk, WorldGenCache cache, Direction dir) {
        if ( !dir.getAxis().isVertical() ) {
            throw new IllegalArgumentException(
                    "Direction input to 'fixAllOffsetsCached' in 'OffsetFeature' must have vertical axis"
            );
        }

        cache.forEachPos( chunk, ( pos ) -> {
            replaceStatesInDir( level, pos, dir );
        } );
    }

    private static void fixSurfaceOffsets( LevelAccessor level, ChunkAccess chunk, Heightmap.Types heightType ) {
        FeatureUtil.forEachSurfaceBlock( level, chunk, heightType, (topPos, minY ) -> {
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
        if ( IOffsetState.shouldBeOnTopState( level, pos, state ) ) {
            level.setBlock( pos, ((IOffsetState) state ).terrain_slabs$getOffsetState(), Block.UPDATE_CLIENTS );
            return true;
        }

        return false;
    }

    // This is a fallback method. Does not work because it cannot be passed a level.
//    private static void fixChunkOffsets( ChunkAccess chunk ) {
//        FeatureUtil.forEachChunkBlock( chunk, Heightmap.Types.WORLD_SURFACE_WG, (pos, maxY ) -> {
//            BlockState state = chunk.getBlockState( pos );
//
//            if ( !( state.getBlock() instanceof SlabBlock ) ) {
//                return; //continue
//            }
//
//            if ( ( state.getValue( SlabBlock.TYPE ) == SlabType.BOTTOM ) ) {
//                replaceStatesInDir( chunk, pos, Direction.UP );
//            }
//        } );
//    }
}
