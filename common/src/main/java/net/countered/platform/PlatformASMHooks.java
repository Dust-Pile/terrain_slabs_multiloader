package net.countered.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class PlatformASMHooks {

    @ExpectPlatform
    public static void defineClass( String name, byte[] clazz ) {
        throw new AssertionError();
    }
}
