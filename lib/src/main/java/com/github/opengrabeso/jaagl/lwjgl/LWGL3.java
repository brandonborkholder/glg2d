package com.github.opengrabeso.jaagl.lwjgl;

import com.github.opengrabeso.jaagl.GL3;

import java.util.HashSet;

public class LWGL3 extends LWGL2GL3 implements GL3 {

    HashSet<String> extensions;

    public LWGL3() {
        super();

        extensions = new HashSet<String>();

        int numExtensions = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL31.GL_NUM_EXTENSIONS);
        for (int i = 0; i < numExtensions; i++) {
            String ext =  org.lwjgl.opengl.GL31.glGetStringi( org.lwjgl.opengl.GL11.GL_EXTENSIONS, i);
            extensions.add(ext);
        }
    }

    @Override
    public boolean isExtensionAvailable(String name) {
        return extensions.contains(name);
    }

    @Override
    public GL3 getGL3() {
        return this;
    }

    @Override
    public int GL_RED() {
        return org.lwjgl.opengl.GL31.GL_RED;
    }
}
