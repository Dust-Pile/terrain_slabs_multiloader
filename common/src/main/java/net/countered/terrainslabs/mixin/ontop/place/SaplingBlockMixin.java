package net.countered.terrainslabs.mixin.ontop.place;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SaplingBlock.class)
public abstract class SaplingBlockMixin extends VegetationBlock {

    protected SaplingBlockMixin(Properties properties) {
        super(properties);
    }

    @Unique
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        if (level.getBlockState(pos).getBlock() instanceof SlabBlock) return false;
        return super.mayPlaceOn(state, level, pos);
    }
}