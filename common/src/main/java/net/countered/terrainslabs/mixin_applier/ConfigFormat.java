package net.countered.terrainslabs.mixin_applier;

// Mimic PlatformConfigHooksImpl @Entry fields
final class ConfigFormat {
    public boolean enableSlabGeneration = true;

    public boolean enableVegetationOnSlabs = true;

    public boolean enableSnowOnSlabs = true;

    public float adjustSlabAo = 0.5f;
}
