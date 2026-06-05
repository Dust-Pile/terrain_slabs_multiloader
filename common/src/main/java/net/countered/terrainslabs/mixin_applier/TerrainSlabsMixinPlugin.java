package net.countered.terrainslabs.mixin_applier;

import dev.architectury.platform.Platform;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

import static net.countered.terrainslabs.mixin_applier.EarlyConfigReader.CTS_CONFIGS;

/**
 * Coarse control over mixin functionality and compatibility through Mixin interface
 */
@SuppressWarnings("unused")
public final class TerrainSlabsMixinPlugin implements IMixinConfigPlugin {
    static final Logger LOGGER = LoggerFactory.getLogger( "terrain_slabs_asm" );
    private static final List<String> ONTOP_VEGETATION_MIXIN_CLASSES = List.of(
            "net.countered.terrainslabs.mixin.ontop.place.MixinBlockBehaviours",
            "net.countered.terrainslabs.mixin.ontop.render.MixinBlockModelShaper",
            "net.countered.terrainslabs.mixin.ontop.render.MixinLevelRenderer",
            "net.countered.terrainslabs.mixin.ontop.state.MixinBlock",
            "net.countered.terrainslabs.mixin.ontop.state.MixinBlockState",
            "net.countered.terrainslabs.mixin.ontop.state.MixinBlockStateBase",
            MixinDirector.DYNAMIC_MIXIN_NAME
    );

    @Override
    public void onLoad( String s ) {}

    /**
     * Disables vegetation mixins on load instead of during play.
     * TODO: much of this can be implemented in better ways, i.e. ASM / state assignment
     * TODO: apply more configs
     */
    @Override
    public boolean shouldApplyMixin( String targetClassName, String mixinClassName ) {
        assert CTS_CONFIGS != null;
        if ( ONTOP_VEGETATION_MIXIN_CLASSES.contains(mixinClassName) ) {
            return CTS_CONFIGS.enableSnowOnSlabs() || CTS_CONFIGS.enableVegetationOnSlabs();
        } else if ( mixinClassName.equals("net.countered.terrainslabs.mixin.ontop.render.MixinBlockStateBaseOcclusion") ) {
            return CTS_CONFIGS.enableSnowOnSlabs();
        } else if ( mixinClassName.equals("net.countered.terrainslabs.mixin.terrain.MixinFlowingFluid") ) {
            return CTS_CONFIGS.fluidsDestroyGeneration() && Platform.getOptionalMod("fluidlogged").isEmpty();
        } else if ( mixinClassName.matches( "net.countered.terrainslabs.mixin.offset.block_tweaks.*" ) ) {
            return CTS_CONFIGS.enableVegetationOnSlabs();
        }

        return true;
    }

    public List<String> getMixins() {
        if ( ClassCacheAccess.isCacheEmpty() ) {
            return null;
        }
        MixinDirector.INSTANCE.define( MixinDirector.DYNAMIC_MIXIN_NAME );
        return List.of( MixinDirector.DYNAMIC_MIXIN_NAME );
    }

    public String getRefMapperConfig() {return null;}
    public void acceptTargets(Set<String> set, Set<String> set1) {}
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
}

