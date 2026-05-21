package net.countered.terrainslabs.block.customslabs.soilslabs;

import net.countered.terrainslabs.block.customslabs.CustomSlab;
import net.countered.terrainslabs.registries.ModBlocksRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;


public class GrassSlab extends SoilSlab {

    public GrassSlab(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        if (state.getValue(TYPE) == SlabType.DOUBLE) {
            super.spawnDestroyParticles(level, player, pos, Blocks.DIRT.defaultBlockState());
        }
        else if (state.getValue(TYPE) == SlabType.TOP) {
            super.spawnDestroyParticles(level, player, pos, ModBlocksRegistry.DIRT_SLAB.get().defaultBlockState().setValue(TYPE, SlabType.TOP));
        }
        else {
            super.spawnDestroyParticles(level, player, pos, ModBlocksRegistry.DIRT_SLAB.get().defaultBlockState());
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!SoilSlab.canBeGrassLike(state, level, pos)) {
            CustomSlab.replaceWithSlabPreserveType(level, pos, state, ModBlocksRegistry.DIRT_SLAB.get());
        } else {
            if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
                BlockState blockState = this.defaultBlockState().setValue(TYPE, level.getBlockState(pos).getValue(TYPE));

                for (int i = 0; i < 4; i++) {
                    BlockPos blockPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    if (level.getBlockState(blockPos).is(ModBlocksRegistry.DIRT_SLAB.get()) && SoilSlab.canPropagateGrassLike(blockState, level, blockPos)) {
                        level.setBlockAndUpdate(blockPos, blockState.setValue(SNOWY, level.getBlockState(blockPos.above()).is(Blocks.SNOW)).setValue(TYPE, level.getBlockState(pos).getValue(TYPE)));
                    }
                }
            }
        }
    }
}
