package net.countered.terrainslabs.mixin_applier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.platform.Platform;
import net.countered.terrainslabs.TerrainSlabs;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

final class EarlyConfigReader {
    /**
     * Configs need to be read early in Fabric without midnight lib for mixin cancel functionality.
     */

    private static final Path filePath = Platform.getConfigFolder().resolve( TerrainSlabs.MOD_ID + ".json" );
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class,
                    new ResourceLocation.Serializer()
            ).create();

    static final ConfigFormat CTS_CONFIGS = load();

    private static ConfigFormat load() {
        try {
            return gson.fromJson(Files.newBufferedReader(filePath), ConfigFormat.class );
        } catch (Exception e) {
            Logger.getAnonymousLogger().info( "Countered's Terrain Slabs unable to read configs early: {}" + e );
            return null;
        }
    }
}
