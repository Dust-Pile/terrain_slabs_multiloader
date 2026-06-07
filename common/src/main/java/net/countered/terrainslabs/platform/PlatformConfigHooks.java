package net.countered.terrainslabs.platform;

public class PlatformConfigHooks {

    private static Provider provider;

    private PlatformConfigHooks() {}

    public static void setProvider(Provider provider) {
        PlatformConfigHooks.provider = provider;
    }

    private static Provider getProvider() {
        if (provider == null) {
            throw new IllegalStateException("PlatformConfigHooks provider not initialized");
        }

        return provider;
    }

    public static boolean isSlabGenerationEnabled() { return getProvider().isSlabGenerationEnabled();}

    public static boolean isVegetationOnSlabsEnabled() { return getProvider().isVegetationOnSlabsEnabled();}

    public static boolean isSnowOnSlabsEnabled() {
        return getProvider().isSnowOnSlabsEnabled();
    }

    public static int getSlabRunLength() {
        return getProvider().getSlabRunLength();
    }

    public static boolean isCornerSlabsEnabled() { return getProvider().isCornerSlabsEnabled();}

    public interface Provider {
        boolean isSlabGenerationEnabled();
        boolean isVegetationOnSlabsEnabled();
        boolean isSnowOnSlabsEnabled();
        int getSlabRunLength();
        boolean isCornerSlabsEnabled();
    }
}


