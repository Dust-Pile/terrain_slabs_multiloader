package net.countered.terrainslabs.mixin_applier;

import net.countered.platform.PlatformASMHooks;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.TraceClassVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.ASM5;

final class ASMTools {
    static final int ASM_VERSION = ASM5;
    static final Logger LOGGER = LoggerFactory.getLogger( "terrain_slabs_asm" );

    public static class MixinGenerator {
        private final String refName;
        private final byte[] clazz;

        public MixinGenerator( String refname ) {
            this.refName = refname;
            this.clazz = null;
        }
        public MixinGenerator( byte[] clazz ) {
            this.refName = null;
            this.clazz = clazz;
        }

        protected final ClassReader getReader() {
            ClassReader reader = null;
            if ( refName != null ) {
                try {
                    ASMTools.LOGGER.info( "Attempting to find class: {}", refName );
                    return new ClassReader( refName );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return new ClassReader( clazz );
            }
        }

        protected void define( String outputName, ASMTools.MixinParamBuilder.MixinParams params ) {
            define( outputName, params, false );
        }
        protected void define( String outputName, ASMTools.MixinParamBuilder.MixinParams params, boolean printDebug ) {
            PlatformASMHooks.defineClass( outputName, inscribeAnnotation( outputName, params ).toByteArray() );

            if ( printDebug ) {
                ClassReader classReader = new ClassReader( inscribeAnnotation( outputName, params ).toByteArray() );
                PrintWriter printWriter = new PrintWriter( System.out );
                TraceClassVisitor traceClassVisitor = new TraceClassVisitor( printWriter );
                ClassVisitor testVisitor = new ClassVisitor( ASM_VERSION, traceClassVisitor ) {};
                classReader.accept( testVisitor, 0 );
            }
        }

        private ClassWriter inscribeAnnotation( String outputName, ASMTools.MixinParamBuilder.MixinParams params ) {
            ClassReader reader = this.getReader();
            ClassWriter writer = new ClassWriter( reader, 0 );

            ClassVisitor annotationModifier = new ASMTools.MixinAnnotationReplacer( writer, params );
            ClassVisitor classCopier = new ASMTools.ClassCopier( outputName, annotationModifier );
            reader.accept( classCopier, 0 );

            // printBytes( writer );
            return writer;
        }
    }

    public static class MixinParamBuilder {
        private List<String> targets;
        private boolean refMap;
        private int priority;

        public MixinParamBuilder() {
            this.targets = List.of();
            this.refMap = true;
            this.priority = 1000;
        }

        public MixinParamBuilder( List<String> targets, boolean refMap, int priority ) {
            this.targets = targets;
            this.refMap = refMap;
            this.priority = priority;
        }

        public MixinParamBuilder setTargets( List<String> targets ) {
            this.targets = targets;
            return this;
        }
        public MixinParamBuilder setRefMap( boolean refMap ) {
            this.refMap = refMap;
            return this;
        }
        public MixinParamBuilder setPriority( int priority ) {
            this.priority = priority;
            return this;
        }

        public MixinParams build() {
            return new MixinParams( this.targets, this.refMap, this.priority );
        }

        protected record MixinParams( List<String> targets, boolean refMap, int priority ) { }
    }

    private static class MixinAnnotationReplacer extends ClassVisitor {
        static final String MIXIN_DESCRIPTOR = Type.getDescriptor( Mixin.class );

        private final List<String> targets;
        private final ClassVisitor classVisitor;
        private final boolean refMap;
        private final int priority;

        protected MixinAnnotationReplacer( ClassVisitor writer, MixinParamBuilder.MixinParams params ) {
            super( ASM_VERSION );
            this.targets = params.targets;
            this.classVisitor = writer;
            super.cv = new ClassNode( ASM_VERSION );
            this.refMap = params.refMap;
            this.priority = params.priority;
        }

        @Override
        public void visit( int version, int access, String name, String signature, String superName, String[] interfaces ) {
            super.visit(version, access, name, signature, superName, interfaces);
            AnnotationNode node = (AnnotationNode) super.cv.visitAnnotation( MIXIN_DESCRIPTOR, false );
            Annotations.setValue( node, "priority", priority );
            Annotations.setValue( node, "refmap", refMap );
            Annotations.setValue( node, "targets", new ArrayList<>( targets ) );
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible ) {
            if ( !descriptor.equals( MIXIN_DESCRIPTOR ) ) {
                return super.visitAnnotation( descriptor, visible );
            }
            return null;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            ( (ClassNode) super.cv ).accept( classVisitor );
        }
    }

    private static class ClassCopier extends ClassVisitor {
        private final String outputName;

        protected ClassCopier( String outputName, ClassVisitor cv ) {
            super( ASM_VERSION );
            this.outputName = outputName;
            super.cv = cv;
        }

        @Override
        public void visit( int version, int access, String name, String signature, String superName, String[] interfaces ) {
            super.visit(version, access, outputName.replace( ".", "/" ), signature, superName, interfaces);
        }
    }

    public static void printBytes( ClassWriter writer ) {
        byte[] code = writer.toByteArray();
        StringBuilder output = new StringBuilder( "[" + code[0] );
        for ( int i = 1; i < code.length; i++ ) {
            output.append(", ").append( code[i] );
        }
        ASMTools.LOGGER.info( output.append( "]" ).toString() );
    }

//    private static String getReferenceName( String name ) {
//        TerrainSlabsMixinPlugin.LOGGER.info(" ========== ATTEMPTING CLASS WALK ========== ");
//        FabricLoader.getInstance().getAllMods().forEach(mod -> {
//            mod.getRootPaths().forEach( jarPath -> {
//                try ( Stream<Path> paths = Files.walk( jarPath ) ) {
//                    for( Path path : paths.toList() ) {
//                        String str = path.toString();
//
//                        if ( str.matches( ".*\\.class$" ) ) {
//                            TerrainSlabsMixinPlugin.LOGGER.info( str );
//                        }
//                    }
//                } catch (Exception e) {
//                    TerrainSlabsMixinPlugin.LOGGER.error( "Failed to walk paths: {}", e.toString() );
//                }
//
//            });
//        });
//
//        if ( !Platform.isDevelopmentEnvironment() && Platform.isFabric() ) {
//            return "knot//" + name;
//        }
//
//        return name;
//    }

}
