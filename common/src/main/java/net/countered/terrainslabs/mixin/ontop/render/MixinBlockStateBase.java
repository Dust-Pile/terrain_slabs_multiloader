package net.countered.terrainslabs.mixin.ontop.render;

import net.countered.terrainslabs.util.MixinHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockStateBase {

    @Shadow
    public abstract boolean triggerEvent(Level level, BlockPos pos, int id, int param);

    /**
     * Mixin for shifting down the collision shape of blocks on slabs, but only if the offset wasn't already applied by the class itself
     */
    @Inject(method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("RETURN"),
            cancellable = true)
    private void terrain_slabs$smartShapeOffset(BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (level instanceof EmptyBlockGetter) {
            return;
        }

        BlockState state = (BlockState) (Object) this;

        if (!MixinHelper.terrain_slabs$isStateValidOnTop(state)) return;

        BlockPos belowPos = pos.below();
        if (state.getBlock() instanceof DoublePlantBlock && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
            belowPos = pos.below(2);
        }

        BlockState belowState = level.getBlockState(belowPos);

        if (!belowState.is(BlockTags.SLABS)) return;
        if (belowState.getValue(SlabBlock.TYPE) != SlabType.BOTTOM) return;

        // fix for flowers moving their shape themselves
        VoxelShape currentShape = cir.getReturnValue();
        if (currentShape.min(Direction.Axis.Y) >= 0) {
            cir.setReturnValue(currentShape.move(0, -0.5, 0));
        }
    }

    /**
     * Method for allowing on top blocks to survive on slabs
     */
    @Inject(method = "canSurvive", at = @At("RETURN"), cancellable = true)
    private void canSurvive(
            LevelReader level,
            BlockPos pos,
            CallbackInfoReturnable<Boolean> cir
    ) {
        BlockState state = (BlockState) (Object) this;
        BlockState belowState = level.getBlockState(pos.below());

        if (belowState.is(BlockTags.SLABS) && MixinHelper.terrain_slabs$isStateValidOnTop(state)) {
            cir.setReturnValue(true);
        }
    }
}