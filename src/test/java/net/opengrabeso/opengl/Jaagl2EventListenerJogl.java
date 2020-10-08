package net.opengrabeso.opengl;

import com.github.opengrabeso.jaagl.jogl.JoGL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

public class Jaagl2EventListenerJogl implements GLEventListener {
    private final Jaagl2EventListener impl;

    Jaagl2EventListenerJogl(Jaagl2EventListener impl) {
        this.impl = impl;
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        final GL2 jgl = glAutoDrawable.getGL().getGL2();
        final com.github.opengrabeso.jaagl.GL2 gl = JoGL.wrap(jgl);
        impl.init(gl);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
        final GL2 jgl = glAutoDrawable.getGL().getGL2();
        final com.github.opengrabeso.jaagl.GL2 gl = JoGL.wrap(jgl);
        impl.dispose(gl);
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        final GL2 jgl = glAutoDrawable.getGL().getGL2();
        final com.github.opengrabeso.jaagl.GL2 gl = JoGL.wrap(jgl);
        impl.display(gl);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
        final GL2 jgl = glAutoDrawable.getGL().getGL2();
        final com.github.opengrabeso.jaagl.GL2 gl = JoGL.wrap(jgl);
        impl.reshape(gl, i, i1, i2, i3);
    }

}
