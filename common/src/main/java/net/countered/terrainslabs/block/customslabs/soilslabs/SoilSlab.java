package net.countered.terrainslabs.block.customslabs.soilslabs;

import net.countered.terrainslabs.block.customslabs.CustomSlab;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class SoilSlab extends CustomSlab {

    public static final BooleanProperty SNOWY;
    static {
        SNOWY = BlockStateProperties.SNOWY;
    }

    public SoilSlab(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(TYPE, SlabType.BOTTOM)
                .setValue(SNOWY, false)
                .setValue(WATERLOGGED, false)
                .setValue(GENERATED, false));
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction == Direction.UP) {
            state = state.setValue(SNOWY, isSnow(neighborState));
        }

        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = context.getLevel().getBlockState(blockPos);
        if (blockState.is(this)) {
            return blockState.setValue(TYPE, SlabType.DOUBLE).setValue(WATERLOGGED, false);
        }
        BlockState blockAbove = context.getLevel().getBlockState(blockPos.above());
        BlockState defaultState = this.defaultBlockState().setValue(SNOWY, isSnow(blockAbove));

        FluidState fluidState = context.getLevel().getFluidState(blockPos);
        BlockState blockState2 = defaultState.setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        Direction direction = context.getClickedFace();
        return direction != Direction.DOWN && (direction == Direction.UP || !(context.getClickLocation().y - blockPos.getY() > 0.5))
                ? blockState2
                : blockState2.setValue(TYPE, SlabType.TOP);

    }

    private static boolean isSnow(BlockState state) {
        return state.is(BlockTags.SNOW);
    }

    /**
     * Shared logic for grass-like blocks (Grass, Mycelium) to determine if they can remain.
     */
    protected static boolean canBeGrassLike(BlockState state, LevelReader levelReader, BlockPos pos) {
        BlockPos blockPos = pos.above();
        BlockState blockState = levelReader.getBlockState(blockPos);
        if (blockState.is(Blocks.SNOW) && (Integer)blockState.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (blockState.getFluidState().getAmount() == 8) {
            return false;
        } else {
            int i = LightEngine.getLightBlockInto(state, Blocks.GRASS_BLOCK.defaultBlockState(), Direction.UP, blockState.getLightBlock());
            return i < 15;
        }
    }

    protected static boolean canPropagateGrassLike(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockPos = pos.above();
        return canBeGrassLike(state, level, pos) && !level.getFluidState(blockPos).is(FluidTags.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SNOWY, TYPE, WATERLOGGED, GENERATED);
    }
}
