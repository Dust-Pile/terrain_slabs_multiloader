package net.countered.platform.forge;

import com.chocohead.mm.api.ClassTinkerers;

public class PlatformASMHooksImpl {

    public static void defineClass( String name, byte[] clazz ) {
        ClassTinkerers.define( name, clazz );
    }
}
