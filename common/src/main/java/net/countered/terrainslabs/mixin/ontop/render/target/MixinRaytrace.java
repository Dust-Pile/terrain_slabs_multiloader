package net.countered.terrainslabs.mixin.ontop.render.target;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockGetter.class)
public interface MixinRaytrace {

    @Shadow
    BlockState getBlockState(BlockPos pos);

    /**
     * Raytrace fix for on top blocks
     */
    @Inject(method = "clip", at = @At("HEAD"), cancellable = true)
    private void clip(ClipContext context, CallbackInfoReturnable<BlockHitResult> cir) {
        BlockGetter self = (BlockGetter) this;

        BlockHitResult result = BlockGetter.traverseBlocks(
                context.getFrom(), context.getTo(), context,

                (ctx, blockPos) -> {
                    BlockState state     = self.getBlockState(blockPos);
                    FluidState fluidState = self.getFluidState(blockPos);
                    Vec3 from = ctx.getFrom(), to = ctx.getTo();

                    VoxelShape blockShape  = ctx.getBlockShape(state, self, blockPos);
                    BlockHitResult hit     = self.clipWithInteractionOverride(from, to, blockPos, blockShape, state);

                    VoxelShape fluidShape  = ctx.getFluidShape(fluidState, self, blockPos);
                    BlockHitResult fluidHit = fluidShape.clip(from, to, blockPos);

                    double dBlock = hit      == null ? Double.MAX_VALUE : from.distanceToSqr(hit.getLocation());
                    double dFluid = fluidHit == null ? Double.MAX_VALUE : from.distanceToSqr(fluidHit.getLocation());
                    BlockHitResult vanillaWinner = dBlock <= dFluid ? hit : fluidHit;

                    BlockPos abovePos    = blockPos.above();
                    BlockState aboveState = self.getBlockState(abovePos);
                    VoxelShape aboveShape = ctx.getBlockShape(aboveState, self, abovePos);
                    BlockHitResult aboveHit = self.clipWithInteractionOverride(from, to, abovePos, aboveShape, aboveState);

                    if (aboveHit != null) {
                        double dAbove = from.distanceToSqr(aboveHit.getLocation());
                        if (dAbove <= dBlock && dAbove <= dFluid) {
                            return aboveHit;
                        }
                    }

                    return vanillaWinner;
                },

                (ctx) -> {
                    Vec3 delta = ctx.getFrom().subtract(ctx.getTo());
                    return BlockHitResult.miss(
                            ctx.getTo(),
                            Direction.getApproximateNearest(delta.x, delta.y, delta.z),
                            BlockPos.containing(ctx.getTo())
                    );
                }
        );

        cir.setReturnValue(result);
    }
}