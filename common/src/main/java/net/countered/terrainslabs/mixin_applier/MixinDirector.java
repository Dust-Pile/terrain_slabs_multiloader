package net.countered.terrainslabs.mixin_applier;

import net.countered.platform.PlatformASMHooks;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.TraceClassVisitor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.ASM5;

// TODO: Add method (or new class) to check if a given class actually needs to be added
class MixinDirector {
    static final String DYNAMIC_MIXIN_NAME_SHORT = "offset.place.MixinBlockBehavioursDynamic";
    static final String DYNAMIC_MIXIN_NAME = "net.countered.terrainslabs.mixin.offset.place.MixinBlockBehavioursDynamic";
    static final String REFERENCE_NAME = "net.countered.terrainslabs.mixin_applier.BlockBehavioursDummyMixin";
    static final String MIXIN_DESCRIPTOR = Type.getDescriptor( Mixin.class );

    protected static final MixinDirector INSTANCE;
    static {
        try {
            INSTANCE = new MixinDirector( REFERENCE_NAME );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    ClassReader reader;
    ClassWriter writer;
    private MixinDirector( String refName ) throws IOException {
        this.reader = new ClassReader( refName );
        this.writer = new ClassWriter( reader, 0 );
    }

    protected void define( String outputName ) {
        PlatformASMHooks.defineClass( outputName, inscribeTargets() );

        // Debug
        ClassReader classReader = new ClassReader( inscribeTargets() );
        PrintWriter printWriter = new PrintWriter( System.out );
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor( printWriter );
        ClassVisitor testVisitor = new ClassVisitor( ASM5, traceClassVisitor ) {};
        classReader.accept( testVisitor, 0 );
    }

    protected static List<String> getTargets() {
        return ClassCacheAccess.getCacheAsList();
    }

    private byte[] inscribeTargets() {
        ClassVisitor classCopier = new ClassCopier( getTargets(), MIXIN_DESCRIPTOR, writer );
        reader.accept( classCopier, 0 );
        return writer.toByteArray();
    }

    private static class ClassCopier extends ClassVisitor {
        private final List<String> targets;
        private final String annotationDescriptor;
        public final ClassVisitor classVisitor;

        protected ClassCopier( List<String> targets, String descriptor, ClassVisitor classVisitor ) {
            super( ASM5 );
            this.annotationDescriptor = descriptor;
            this.targets = targets;
            this.classVisitor = classVisitor;
            super.cv = new ClassNode( ASM5 );
        }

        @Override
        public void visit( int version, int access, String name, String signature, String superName, String[] interfaces ) {
            super.visit(version, access, DYNAMIC_MIXIN_NAME.replace( ".", "/" ), signature, superName, interfaces);
            AnnotationNode node = (AnnotationNode) super.cv.visitAnnotation( annotationDescriptor, false );
            Annotations.setValue( node, "priority", 1200 );
            Annotations.setValue( node, "refmap", false );
            Annotations.setValue( node, "targets", new ArrayList<>( targets ) );
        }

        @Override
        public AnnotationVisitor visitAnnotation( String descriptor, boolean visible ) {
            if ( !descriptor.equals( annotationDescriptor ) ) {
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
}
