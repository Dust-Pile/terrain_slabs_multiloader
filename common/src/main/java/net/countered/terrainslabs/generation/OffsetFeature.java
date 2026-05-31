package net.countered.terrainslabs.generation;

import com.mojang.serialization.Codec;
import net.countered.platform.PlatformConfigHooks;
import net.countered.terrainslabs.block.interfaces.IOffsetState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class OffsetFeature extends Feature<NoneFeatureConfiguration> {

    public static final WorldGenCache BOTTOM_SLAB_CACHE = new WorldGenCache();
//            ( cache, chunk ) -> {
//                fixChunkOffsetsCached( cache.getLevel( chunk ), chunk.getPos().getWorldPosition(), cache, Direction.UP );
//            } );

    public OffsetFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place( FeaturePlaceContext<NoneFeatureConfiguration> context ) {
        return true;
    }

    public static void run( ProtoChunk chunk, LevelChunk levelChunk ) {
        if ( !PlatformConfigHooks.isSlabGenerationEnabled()
                || ( !PlatformConfigHooks.isSnowOnSlabsEnabled() && !PlatformConfigHooks.isVegetationOnSlabsEnabled() )
        ) {
            return;
        }

        LevelAccessor level = levelChunk.getLevel();
        if ( BOTTOM_SLAB_CACHE.containsChunk( chunk ) ) {
            fixChunkOffsetsCached( level, chunk, BOTTOM_SLAB_CACHE, Direction.UP );
            fixSurfaceOffsets( level, chunk, Heightmap.Types.MOTION_BLOCKING );
        } else {
            fixChunkOffsets( level, chunk );
        }

        BOTTOM_SLAB_CACHE.removeChunk( chunk );
        //BOTTOM_SLAB_CACHE.getNeighbors( chunk ).forEach( BOTTOM_SLAB_CACHE::tryTrimChunks );
    }

    private static void fixChunkOffsetsCached(LevelAccessor level, ChunkAccess chunk, WorldGenCache cache, Direction dir) {
        if ( !dir.getAxis().isVertical() ) {
            throw new IllegalArgumentException(
                    "Direction input to 'fixAllOffsetsCached' in 'OffsetFeature' must have vertical axis"
            );
        }

        cache.forExistingQueue( chunk, ( pos ) -> {
            replaceStatesInDir( level, pos, dir );
//            if ( ! ) {
//                cache.addBlockPos( level, pos );
//            }
        } );
    }

    // This is a fallback method (not preferred)
    private static void fixChunkOffsets( LevelAccessor level, ChunkAccess chunk ) {
        FeatureUtil.forEachChunkBlock( level, chunk, Heightmap.Types.WORLD_SURFACE_WG, (pos, maxY ) -> {
            BlockState state = level.getBlockState( pos );

            if ( !( state.getBlock() instanceof SlabBlock ) ) {
                return; //continue
            }

            if ( ( state.getValue( SlabBlock.TYPE ) == SlabType.BOTTOM ) ) {
                replaceStatesInDir( level, pos, Direction.UP );
                // We're here, may as well populate the cache.
                //BOTTOM_SLAB_CACHE.addBlockPos( level, pos );
            }
        } );

        // Ensure cache for chunk exists
        // BOTTOM_SLAB_CACHE.mapChunk( level, chunk );
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
        return IOffsetState.shouldBeOnTopState( level, pos, state )
                && level.setBlock( pos, ((IOffsetState) state ).terrain_slabs$getOffsetState(), Block.UPDATE_CLIENTS );
    }
}
