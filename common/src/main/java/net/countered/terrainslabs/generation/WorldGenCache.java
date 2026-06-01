package net.countered.terrainslabs.generation;

import net.countered.terrainslabs.TerrainSlabs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WorldGenCache {

    // Should not be a memory leak...
    final private static int INITIAL_WARNING_SIZE = 250;
    final private double GROWTH_FACTOR = 1.5;
    private int warningSize = INITIAL_WARNING_SIZE;
    final private Map<ChunkAccess, Pair<ArrayList<BlockPos>, LevelAccessor>> CACHE = new HashMap<>( INITIAL_WARNING_SIZE );

    public WorldGenCache() {}

    public boolean containsChunk( ChunkAccess chunk ) {
        return CACHE.containsKey( chunk );
    }

    public <L extends LevelAccessor> void addBlockPos(L level, BlockPos pos ) {
        ChunkAccess chunk = level.getChunk( pos );

        if ( containsChunk( chunk ) ) {
            CACHE.get( chunk ).getA().add( pos );
        } else if ( mapChunk( level, chunk ) ) {
            CACHE.get( chunk ).getA().add( pos );
        }
    }

    public void forEachPos( ChunkAccess chunk, Consumer<BlockPos> handler ) {
        if( !containsChunk( chunk ) ) {
            return;
        }

        ArrayList<BlockPos> list = this.getList( chunk );
        assert list != null;
        for ( BlockPos pos : list ) {
            handler.accept( pos );
        }
    }

    private ArrayList<BlockPos> getList( ChunkAccess chunk ) {
        if ( containsChunk( chunk ) ) {
            return CACHE.get( chunk ).getA();
        }

        return null;
    }

    protected LevelAccessor getLevel( ChunkAccess chunk ) {
        if ( containsChunk( chunk ) ) {
            return CACHE.get( chunk ).getB();
        }

        return null;
    }

    protected boolean mapChunk( LevelAccessor level, ChunkAccess chunk ) {
        if ( containsChunk( chunk ) || chunk.getStatus().isOrAfter( ChunkStatus.INITIALIZE_LIGHT ) ) {
            return false;
        }

        if ( CACHE.size() >= warningSize) {
            trimCache();
        }

        CACHE.put( chunk, new Pair<>( new ArrayList<>( 200 ), level ) );
        return true;
    }

    protected void removeChunk( ChunkAccess chunk ) {
        CACHE.remove( chunk );
    }

    /**
     * Should not normally occur. Will cause offsets to fail
     */
    private void trimCache() {
        List<ChunkAccess> chunksToRemove = new ArrayList<>();
        CACHE.forEach( (chunk, stack ) -> {
            if ( shouldTrimChunk( chunk ) ) chunksToRemove.add( chunk );
        } );

        chunksToRemove.forEach( this::removeChunk );
        TerrainSlabs.LOGGER.warn( "Placed Slab Cache grew to size {}; trimmed to size {}.", warningSize, CACHE.size() );
        warningSize = Math.max( (int) ( CACHE.size() * GROWTH_FACTOR ), INITIAL_WARNING_SIZE );
    }

    private boolean shouldTrimChunk(ChunkAccess chunk ) {
        if ( !containsChunk( chunk ) ) {
            return false;
        }

        return chunk.getStatus().isOrAfter( ChunkStatus.SPAWN );
    }
}
