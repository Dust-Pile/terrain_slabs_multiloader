package net.countered.terrainslabs.registries;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class ModOnTopBlocksRegistry {

    private static final List<Block> ON_TOP_BLOCKS = new ArrayList<>(
            List.of(Blocks.SNOW)
    );

    public static void addOnTopBlock(Block block) {
        ON_TOP_BLOCKS.add(block);
    }

    public static List<Block> getOnTopBlocks() {
        return ON_TOP_BLOCKS;
    }
}
