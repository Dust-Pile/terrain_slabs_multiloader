package net.countered.terrainslabs.generation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class FeatureUtil {

    public static void iterateChunkBlocks( WorldGenLevel level, BlockPos origin, BiConsumer<BlockPos, Integer> handler ) {
        ChunkPos chunkPos = new ChunkPos(origin);
        level.getChunk( chunkPos.x, chunkPos.z );

        int minY = level.getMinBuildHeight();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkPos.getMinBlockX() + x;
                int worldZ = chunkPos.getMinBlockZ() + z;
                int maxY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, worldX, worldZ);
                for (int y = maxY; y >= minY; y--) {
                    BlockPos currentPos = new BlockPos(worldX, y, worldZ);
                    handler.accept( currentPos, minY );
                }
            }
        }
    }

    public static void iterateAbove(WorldGenLevel level, BlockPos pos, int maxY, BiFunction<BlockPos, BlockState, Boolean> func) {
        while ( pos.getY() <= maxY && func.apply( pos, level.getBlockState( pos ) ) ) {
            pos = pos.above();
        }
    }

    public static void iterateBelow(WorldGenLevel level, BlockPos pos, int minY, BiFunction<BlockPos, BlockState, Boolean> func) {
        while ( pos.getY() >= minY && func.apply( pos, level.getBlockState( pos ) ) ) {
            pos = pos.below();
        }
    }
}
