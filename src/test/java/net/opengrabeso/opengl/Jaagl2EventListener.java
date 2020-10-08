package net.opengrabeso.opengl;

import java.util.EventListener;

public interface Jaagl2EventListener extends EventListener {
    void init(com.github.opengrabeso.jaagl.GL2 var1);

    void dispose(com.github.opengrabeso.jaagl.GL2 var1);

    void display(com.github.opengrabeso.jaagl.GL2 var1);

    void reshape(com.github.opengrabeso.jaagl.GL2 var1, int var2, int var3, int var4, int var5);
}
