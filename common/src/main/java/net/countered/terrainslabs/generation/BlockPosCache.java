package net.countered.terrainslabs.generation;

import net.countered.terrainslabs.TerrainSlabs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public final class BlockPosCache {

    // Should not be a memory leak... should not be...
    private static int warningSize = 100;
    final protected static Map<ChunkAccess, Stack<BlockPos>> PLACED_SLABS = new HashMap<>();

    public static <L extends LevelAccessor> void addSlabPos( L level, BlockPos pos ) {
        ChunkAccess chunk = level.getChunk( pos );

        if ( PLACED_SLABS.containsKey( chunk ) ) {
            PLACED_SLABS.get( chunk ).push( pos );
        } else {
            mapChunk( chunk );
            PLACED_SLABS.get( chunk ).push( pos );
        }
    }

    protected static Optional<Stack<BlockPos>> getChunk( ChunkAccess chunk ) {
        if ( PLACED_SLABS.containsKey( chunk ) ) {
            return Optional.of( PLACED_SLABS.get( chunk ) );
        }

        return Optional.empty();
    }

    protected static Optional<Stack<BlockPos>> popChunk( ChunkAccess chunk ) {
        Optional<Stack<BlockPos>> stackOption = getChunk( chunk );
        PLACED_SLABS.remove( chunk );

        return stackOption;
    }

    private static void mapChunk( ChunkAccess chunk ) {
        if ( PLACED_SLABS.containsKey( chunk ) || chunk.getStatus().isOrAfter( ChunkStatus.INITIALIZE_LIGHT ) ) {
            return;
        }

        if ( PLACED_SLABS.size() >= warningSize) {
            defragCache();
        }

        PLACED_SLABS.put( chunk, new Stack<>() );
    }

    private static void defragCache() {
        PLACED_SLABS.forEach( ( chunk, stack ) -> {
            if ( chunk.getStatus().isOrAfter( ChunkStatus.INITIALIZE_LIGHT ) ) {
                PLACED_SLABS.remove( chunk );
            }
        } );

        TerrainSlabs.LOGGER.warn( "Placed Slab Cache grew to size {}; trimmed to size {}.", warningSize, PLACED_SLABS.size() );
        warningSize = Math.max( PLACED_SLABS.size() * 2, 100 );
    }
}
