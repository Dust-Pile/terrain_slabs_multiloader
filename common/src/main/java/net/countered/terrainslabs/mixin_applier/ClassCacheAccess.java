package net.countered.terrainslabs.mixin_applier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.platform.Platform;
import net.countered.terrainslabs.TerrainSlabs;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

// TODO: Add method (or new class) to check if a given class is available
public class ClassCacheAccess {
    private static final Path CACHE_PATH = Platform.getConfigFolder().resolve( TerrainSlabs.MOD_ID + "/class_cache.json" );
    private static final Path CACHE_DIR = Platform.getConfigFolder().resolve( TerrainSlabs.MOD_ID );
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final Set<String> CTS_CLASS_CACHE = new HashSet<>( loadClassCache().classesWithOffsetBlocks );
    private static final Set<String> newClasses = new HashSet<>();
    private static boolean hasNewClasses = false;

    public static List<String> getCacheAsList() {
        return CTS_CLASS_CACHE.stream().toList();
    }
    public static boolean cacheContains(String clazz ) {
        return CTS_CLASS_CACHE.contains( clazz );
    }
    public static boolean isCacheEmpty() {
        return CTS_CLASS_CACHE.isEmpty();
    }

    public static boolean addToCache( String clazz ) {
        String[] elements = clazz.split( "\\." );
        if ( elements.length < 2 || elements[1].equals( "minecraft" ) ) {
            // Exclude minecraft classes. Only things with no refmap should be added dynamically
            return false;
        }

        TerrainSlabsMixinPlugin.LOGGER.info( clazz );
        if ( !cacheContains( clazz ) && newClasses.add( clazz ) ) {
            hasNewClasses = true;
            return true;
        }

        return false;
    }
    public static boolean hasNewClasses() {
        return hasNewClasses;
    }

    private static ClassNameCache loadClassCache() {
        try {
            return gson.fromJson( Files.newBufferedReader( CACHE_PATH ), ClassNameCache.class );
        } catch ( Exception e ) {
            Logger.getAnonymousLogger().info( "Countered's Terrain Slabs unable to read class cache: {}" + e );
            writeClasses( List.of() );

            return new ClassNameCache();
        }
    }

    public static boolean writeClasses() {
        if ( !hasNewClasses() ) {
            return false;
        }
        List<String> allClasses = new ArrayList<>( getCacheAsList() );
        allClasses.addAll( newClasses );
        return writeClasses( allClasses );
    }

    private static boolean writeClasses( List<String> classNames ) {
        try {
            if ( !Files.exists( CACHE_PATH ) && !CACHE_DIR.toFile().mkdir() && !CACHE_PATH.toFile().createNewFile() ) {
                return false;
            }

            Files.write( CACHE_PATH, gson.toJson( new ClassNameCache( classNames ) ).getBytes() );
            return true;
        } catch ( Exception e ) {
            TerrainSlabsMixinPlugin.LOGGER.error( "Failed to write file for cache: {}", e.toString() );
            return false;
        }
    }

    public record ClassNameCache(
            List<String> classesWithOffsetBlocks
    ) {
        public ClassNameCache() {
            this( List.of() );
        }
    }
}
