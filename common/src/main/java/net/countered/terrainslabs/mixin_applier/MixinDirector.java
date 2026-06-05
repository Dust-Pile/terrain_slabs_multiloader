package net.countered.terrainslabs.mixin_applier;

import net.countered.platform.PlatformASMHooks;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;

import java.util.List;

import static org.objectweb.asm.Opcodes.ASM5;

// TODO: Add method (or new class) to check if a given class actually needs to be added
class MixinDirector {
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
    private ClassReader reader;
    private ClassWriter writer;
    private MixinDirector( String refName ) throws IOException {
        this.reader = new ClassReader( refName );
        this.writer = new ClassWriter( reader, 0 );
    }

    protected void define( String outputName ) {
        PlatformASMHooks.defineClass( outputName, inscribeTargets() );
    }

    protected static List<String> getTargets() {
        return ClassCacheAccess.getCacheAsList();
    }

    private byte[] inscribeTargets() {
        ClassVisitor targetInscriber = new TargetInscriber( getTargets(), MIXIN_DESCRIPTOR, writer );
        reader.accept( targetInscriber, 0 );
        return writer.toByteArray();
    }

    private static class TargetInscriber extends ClassNode {
        private final List<String> targets;
        private final String annotationDescriptor;
        private AnnotationNode node;

        protected TargetInscriber( List<String> targets, String descriptor, ClassVisitor cv ) {
            super( ASM5 );
            this.targets = targets;
            this.annotationDescriptor = descriptor;
            this.cv = cv;
        }

        @Override
        public AnnotationVisitor visitAnnotation( String descriptor, boolean visible) {
            AnnotationVisitor node = super.visitAnnotation( descriptor, visible );
            if ( descriptor.equals( annotationDescriptor ) ) {
                this.node = (AnnotationNode) node;
            }
            return node;
        }

        @Override
        public void visitEnd() {
            Annotations.setValue( this.node, "targets", targets );
            cv.visitEnd();
        }
    }
}
