package net.countered.terrainslabs.fabric;

import eu.midnightdust.lib.config.MidnightConfig;
import net.countered.terrainslabs.TerrainSlabs;
import net.countered.terrainslabs.fabric.feature.ModAddedFeatures;
import net.countered.terrainslabs.platform.PlatformConfigHooks;
import net.countered.terrainslabs.platform.fabric.PlatformConfigHooksImpl;
import net.countered.terrainslabs.registries.ModFlattenablesRegistry;
import net.countered.terrainslabs.registries.ModTillableRegistry;
import net.fabricmc.api.ModInitializer;

public final class TerrainSlabsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        // Run our common setup.
        TerrainSlabs.init();

        PlatformConfigHooks.setProvider(new PlatformConfigHooks.Provider() {
            @Override
            public boolean isSlabGenerationEnabled() {
                return PlatformConfigHooksImpl.isSlabGenerationEnabled();
            }

            @Override
            public boolean isVegetationOnSlabsEnabled() {
                return PlatformConfigHooksImpl.isVegetationOnSlabsEnabled();
            }

            @Override
            public boolean isSnowOnSlabsEnabled() {
                return PlatformConfigHooksImpl.isSnowOnSlabsEnabled();
            }

            @Override
            public int getSlabRunLength() {
                return PlatformConfigHooksImpl.getSlabRunLength();
            }

            @Override
            public boolean isCornerSlabsEnabled() {
                return PlatformConfigHooksImpl.isCornerSlabsEnabled();
            }
        });

        ModFlattenablesRegistry.registerFlattenables();
        ModTillableRegistry.registerTillables();

        ModAddedFeatures.registerFeatures();
        MidnightConfig.init(TerrainSlabs.MOD_ID, PlatformConfigHooksImpl.class);

    }
}
