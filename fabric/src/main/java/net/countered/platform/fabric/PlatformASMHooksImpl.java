package net.countered.platform.fabric;

import com.chocohead.mm.api.ClassTinkerers;

public class PlatformASMHooksImpl {

    public static void defineClass( String name, byte[] clazz ) {
        ClassTinkerers.define( name, clazz );
    }
}
