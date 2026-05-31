package net.countered.terrainslabs.mixin.feature;

import net.countered.terrainslabs.generation.OffsetFeature;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( ChunkMap.class )
public class MixinChunkMap {

    @Inject( method = "method_31415", at = @At( "TAIL" ) )
    private void terrain_slabs$postprocessOffset(
            ProtoChunk protoChunk, LevelChunk levelChunk, CallbackInfo ci
    ) {
        OffsetFeature.run( protoChunk, levelChunk );
    }
}
