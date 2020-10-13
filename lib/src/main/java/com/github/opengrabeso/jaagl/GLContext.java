package com.github.opengrabeso.jaagl;

import com.github.opengrabeso.jaagl.jogl.JoGL;

public abstract class GLContext {

    public static GL getCurrentGL() {
        // TODO: avoid whenever possible, pass GL instead
        com.jogamp.opengl.GL currentGL = com.jogamp.opengl.GLContext.getCurrentGL();

        return JoGL.wrap(currentGL);
    }
}
