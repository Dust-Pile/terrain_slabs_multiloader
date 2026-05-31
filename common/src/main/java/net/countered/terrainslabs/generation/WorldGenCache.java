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

    // Should not be a memory leak... should not be...
    final private static int INITIAL_WARNING_SIZE = 100;
    final private double GROWTH_FACTOR = 1.5;
    private int warningSize = INITIAL_WARNING_SIZE;
    final protected Map<ChunkAccess, Pair<Queue<BlockPos>, LevelAccessor>> CACHE = new HashMap<>( INITIAL_WARNING_SIZE );
    final BiConsumer<WorldGenCache, ChunkAccess> exitTask;

    public WorldGenCache() {
        this.exitTask = ( cache, chunk ) -> {};
    }

    public WorldGenCache(BiConsumer<WorldGenCache, ChunkAccess> exitTask ) {
        this.exitTask = exitTask;
    }

    public boolean containsChunk( ChunkAccess chunk ) {
        return CACHE.containsKey( chunk );
    }

    public <L extends LevelAccessor> void addBlockPos(L level, BlockPos pos ) {
        ChunkAccess chunk = level.getChunk( pos );

        if ( containsChunk( chunk ) ) {
            CACHE.get( chunk ).getA().offer( pos );
        } else if ( mapChunk( level, chunk ) ) {
            CACHE.get( chunk ).getA().offer( pos );
        }
    }

    /**
     * Explicitly allows adding more items to queue. Will only run for item at time of start.
     */
    public void forExistingQueue( ChunkAccess chunk, Consumer<BlockPos> handler ) {
        if( !containsChunk( chunk ) ) {
            return;
        }

        Queue<BlockPos> queue = this.getQueue( chunk );
        assert queue != null;
        for (int i = queue.size(); i > 0; i-- ) {
            handler.accept( queue.poll() );
        }
    }

    private Queue<BlockPos> getQueue( ChunkAccess chunk ) {
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

        CACHE.put( chunk, new Pair<>( new LinkedList<>(), level ) );
        return true;
    }

    private void trimCache() {
        List<ChunkAccess> chunksToRemove = new ArrayList<>();
        CACHE.forEach( (chunk, stack ) -> {
            if ( shouldTrimChunk( chunk ) ) chunksToRemove.add( chunk );
        } );

        chunksToRemove.forEach( this::removeChunk );
        TerrainSlabs.LOGGER.warn( "Placed Slab Cache grew to size {}; trimmed to size {}.", warningSize, CACHE.size() );
        warningSize = Math.max( (int) ( CACHE.size() * GROWTH_FACTOR ), INITIAL_WARNING_SIZE );
    }

    protected void tryTrimChunks( ChunkAccess origin ) {
        Collection<ChunkAccess> neighbors = this.getNeighbors( origin );
        if ( shouldTrimChunk( origin ) ) {
            tryTrimChunk( origin );
            neighbors.forEach( this::tryTrimChunks );
        }
    }

    protected void tryTrimChunk( ChunkAccess chunk ) {
        if ( shouldTrimChunk( chunk ) ) {
            removeChunk( chunk );
        }
    }

    protected void removeChunk( ChunkAccess chunk ) {
        exitTask.accept( this, chunk );
        CACHE.remove( chunk );
    }

    private boolean shouldTrimChunk(ChunkAccess chunk ) {
        if ( !containsChunk( chunk ) ) {
            return false;
        }

        return chunk.getStatus().isOrAfter( ChunkStatus.SPAWN );
    }

    protected Collection<ChunkAccess> getNeighbors( ChunkAccess chunk ) {
        if ( !containsChunk( chunk ) ) {
            return List.of();
        }

        Collection<ChunkAccess> output = new ArrayList<>();
        LevelAccessor level = this.getLevel( chunk );
        int centerX = chunk.getPos().x;
        int centerZ = chunk.getPos().z;

        int size = 1;
        for ( int x = size; x >= -size; x-- ) {
            for ( int z = size; z >= -size; z-- ) {
                if ( x == 0 && z == 0 ) continue;
                output.add( level.getChunk( centerX + x, centerZ + z ) );
            }
        }

        return output;
    }
}
