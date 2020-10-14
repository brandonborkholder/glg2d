package net.opengrabeso.opengl;

import com.github.opengrabeso.jaagl.GL2GL3;
import com.github.opengrabeso.jaagl.jogl.JoGL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

public class Jaagl2EventListenerJogl implements GLEventListener {
    private final Jaagl2EventListener impl;

    Jaagl2EventListenerJogl(Jaagl2EventListener impl) {
        this.impl = impl;
    }

    private static GL2GL3 getGL(GLAutoDrawable glAutoDrawable) {
        return JoGL.wrap(glAutoDrawable.getGL().getGL3());
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        final GL2GL3 gl = getGL(glAutoDrawable);
        impl.init(gl);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
        final GL2GL3 gl = getGL(glAutoDrawable);
        impl.dispose(gl);
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        final GL2GL3 gl = getGL(glAutoDrawable);
        impl.display(gl);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
        final GL2GL3 gl = getGL(glAutoDrawable);
        impl.reshape(gl, i, i1, i2, i3);
    }

}
