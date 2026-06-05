package net.countered.terrainslabs.generation;

import net.countered.platform.PlatformConfigHooks;
import net.countered.terrainslabs.TerrainSlabs;
import net.countered.terrainslabs.block.interfaces.IOffsetState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;

/**
 * Did not work when run as a feature; missed too many things despite my efforts. Run as a custom final step instead.
 */
final public class OffsetFeature {

    public static final WorldGenCache BOTTOM_SLAB_CACHE = new WorldGenCache();

    public static void run( ServerLevel serverLevel, ProtoChunk chunk ) {
        if ( !PlatformConfigHooks.isSlabGenerationEnabled()
                || ( !PlatformConfigHooks.isSnowOnSlabsEnabled() && !PlatformConfigHooks.isVegetationOnSlabsEnabled() )
        ) {
            return;
        }

        LevelAccessor level = new WorldGenRegion( serverLevel, List.of( chunk ), ChunkStatus.SPAWN, 0 );
        if ( !BOTTOM_SLAB_CACHE.containsChunk( chunk ) ) {
            fixChunkOffsets( level, chunk );
            TerrainSlabs.LOGGER.info( "Loaded chunk uncached." );
        } else {
            fixSurfaceOffsets( level, chunk, Heightmap.Types.MOTION_BLOCKING );
            fixChunkOffsetsCached( level, chunk, BOTTOM_SLAB_CACHE, Direction.UP );
            BOTTOM_SLAB_CACHE.removeChunk( chunk );
        }
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

    // This is a fallback method. Cached version runs faster but is not always available.
    private static void fixChunkOffsets( LevelAccessor level, ChunkAccess chunk ) {
        FeatureUtil.forEachChunkBlock( level, chunk, Heightmap.Types.WORLD_SURFACE_WG, (pos, maxY ) -> {
            BlockState state = level.getBlockState( pos );

            if ( !( state.getBlock() instanceof SlabBlock) ) {
                return; //continue
            }

            if ( ( state.getValue( SlabBlock.TYPE ) == SlabType.BOTTOM ) ) {
                replaceStatesInDir( level, pos, Direction.UP );
            }
        } );
    }

    private static void fixSurfaceOffsets( LevelAccessor level, ChunkAccess chunk, Heightmap.Types heightType ) {
        FeatureUtil.forEachSurfaceBlock( level, chunk, heightType, (topPos, minY ) -> {
            replaceStatesInDir( level, topPos, Direction.UP );
        } );
    }

    private static void replaceStatesInDir(LevelAccessor level, BlockPos pos, Direction dir ) {
        FeatureUtil.iterateDirUntilFail(level, pos.relative(dir), dir,
                (aPos, aState) -> placeOntopState(level, aPos, aState)
        );
    }

    // TODO: Rename and handle onbottom states
    private static boolean placeOntopState( LevelAccessor level, BlockPos pos, BlockState state ) {
        if ( IOffsetState.shouldBeOnTopState( level, pos, state ) ) {
            state = ((IOffsetState) state ).terrain_slabs$getOffsetState();
            return level.setBlock( pos, state, Block.UPDATE_CLIENTS );
        }

        return false;
    }
}
