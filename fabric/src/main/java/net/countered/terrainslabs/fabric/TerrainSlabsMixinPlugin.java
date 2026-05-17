package net.countered.terrainslabs.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.countered.terrainslabs.TerrainSlabs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class TerrainSlabsMixinPlugin implements IMixinConfigPlugin {
    /**
     * Configs need to be read early in Fabric without midnight lib for mixin cancel functionality.
     */
    private static ConfigReader reader;
    private static final Path filePath = FabricLoader.getInstance().getConfigDir().resolve( TerrainSlabs.MOD_ID + ".json" );
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class,
                    new ResourceLocation.Serializer()
            ).create();

    static {
        loadValuesFromJson();
    }

    private static final List<String> ONTOP_VEGETATION_MIXIN_CLASSES = List.of(
            "net.countered.terrainslabs.mixin.ontop.place.MixinBlockBehaviours",
            "net.countered.terrainslabs.mixin.ontop.render.MixinBlockModelShaper",
            "net.countered.terrainslabs.mixin.ontop.render.MixinLevelRenderer",
            "net.countered.terrainslabs.mixin.ontop.state.MixinBlock",
            "net.countered.terrainslabs.mixin.ontop.state.MixinBlockState",
            "net.countered.terrainslabs.mixin.ontop.state.MixinBlockStateBase"
    );

    private static void loadValuesFromJson() {
        try {
            reader = gson.fromJson(Files.newBufferedReader(filePath), ConfigReader.class );
        } catch (Exception e) {
            Logger.getAnonymousLogger().info( "Countered's Terrain Slabs unable to read configs early: {}" + e );
        }
    }

    @Override
    public void onLoad( String s ) {}

    /**
     * Disables vegetation mixins on load instead of during play.
     */
    @Override
    public boolean shouldApplyMixin( String targetClassName, String mixinClassName ) {
        if ( !reader.enableVegetationOnSlabs && ONTOP_VEGETATION_MIXIN_CLASSES.contains( mixinClassName ) ) {
            return false;
        }
        return true;
    }

    // Mimic PlatformConfigHooksImpl @Entry fields
    private static class ConfigReader {
        public boolean enableSlabGeneration = true;

        public boolean enableVegetationOnSlabs = true;

        public boolean enableSnowOnSlabs = true;

        public float adjustSlabAo = 0.5f;
    }

    public String getRefMapperConfig() {return null;}
    public void acceptTargets(Set<String> set, Set<String> set1) {}
    public List<String> getMixins() {return null;}
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
}

