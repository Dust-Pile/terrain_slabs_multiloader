//package net.countered.terrainslabs.fabric.mixin;
//
//import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;
//import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractTerrainRenderContext;
//import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.block.SlabBlock;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.properties.SlabType;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(value = AbstractTerrainRenderContext.class, remap = false)
//public class MixinAbstractTerrainRenderContext {
//
//    @Shadow @Final protected BlockRenderInfo blockInfo;
//
//    @Inject(
//        method = "bufferQuad(Lnet/fabricmc/fabric/impl/client/indigo/renderer/mesh/MutableQuadViewImpl;)V",
//        at = @At("HEAD")
//    )
//    private void offsetSlabAoBefore(MutableQuadViewImpl quad, CallbackInfo ci) {
//        BlockPos pos = blockInfo.blockPos;
//        BlockState below = blockInfo.blockView.getBlockState(pos.below());
//
//        // Prüfen, ob der Block direkt über einer unteren Slab ist
//        boolean slabBelow = below.getBlock() instanceof SlabBlock
//                && below.getValue(SlabBlock.TYPE) == SlabType.BOTTOM;
//
//        if (slabBelow) {
//            // Wir tricksen Indigo aus, indem wir die interne BlockPos für diesen Quad temporär nach unten verschieben
//            // ACHTUNG: blockInfo.blockPos ist meistens veränderbar (MutableBlockPos),
//            // falls nicht, müssen wir per Accessor das Feld austauschen. Bei Indigo ist es eine feste Referenz, die wir mutieren können.
//            ((BlockPos.MutableBlockPos) blockInfo.blockPos).move(0, -1, 0);
//        }
//    }
//
//    @Inject(
//        method = "bufferQuad(Lnet/fabricmc/fabric/impl/client/indigo/renderer/mesh/MutableQuadViewImpl;)V",
//        at = @At("RETURN")
//    )
//    private void offsetSlabAoAfter(MutableQuadViewImpl quad, CallbackInfo ci) {
//        // Unbedingt die Position wieder zurücksetzen, sonst verschiebt sich der nächste Block mit!
//        BlockPos pos = blockInfo.blockPos;
//        BlockState below = blockInfo.blockView.getBlockState(pos.above()); // Weil wir einen runter sind, ist die "Slab" jetzt drüber
//
//        boolean slabBelow = below.getBlock() instanceof SlabBlock
//                && below.getValue(SlabBlock.TYPE) == SlabType.BOTTOM;
//
//        if (slabBelow) {
//            ((BlockPos.MutableBlockPos) blockInfo.blockPos).move(0, 1, 0);
//        }
//    }
//}