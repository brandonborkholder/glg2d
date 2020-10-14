package com.github.opengrabeso.jaagl.jogl;

import com.github.opengrabeso.jaagl.*;

public class JoGL3 extends JoGL2GL3 implements GL3 {
    private com.jogamp.opengl.GL3 ggl() {
        return (com.jogamp.opengl.GL3) this.gl;
    }

    public JoGL3(com.jogamp.opengl.GL3 gl) {
        super(gl);
    }

    @Override
    public GL3 getGL3() {
        return this;
    }

    @Override
    public int GL_RED() {
        return com.jogamp.opengl.GL3.GL_RED;
    }
}
