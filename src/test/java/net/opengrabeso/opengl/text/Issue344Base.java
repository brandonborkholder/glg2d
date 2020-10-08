package net.opengrabeso.opengl.text;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Arrays;

import com.github.opengrabeso.jaagl.jogl.JoGL;
import com.jogamp.common.util.InterruptSource;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.*;
import net.opengrabeso.opengl.Jaagl2EventListener;
import net.opengrabeso.opengl.SelectJaaglEventListener;
import net.opengrabeso.opengl.util.awt.TextRenderer;
import org.lwjgl.glfw.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.glfw.GLFW.*;


import java.util.EventListener;


/** Test Code adapted from TextCube.java (in JOGL demos)
 *
 * @author spiraljetty
 * @author kbr
 */

public abstract class Issue344Base implements Jaagl2EventListener
{
    GLU glu = new GLU();
    TextRenderer renderer;

    float textScaleFactor;
    Font font;
    boolean useMipMaps;

    protected Issue344Base() {
        font = new Font("default", Font.PLAIN, 200);
        useMipMaps = true; //false
    }

    protected abstract String getText();

    protected void run(final String[] args) {
        SelectJaaglEventListener jaaglEventListener = new SelectJaaglEventListener(this);
        jaaglEventListener.run(args);
    }

    @Override
    public void init(final com.github.opengrabeso.jaagl.GL2 gl) {
        gl.glEnable(GL.GL_DEPTH_TEST);

        renderer = new TextRenderer(gl, font, useMipMaps);

        final Rectangle2D bounds = renderer.getBounds(getText());
        final float w = (float) bounds.getWidth();
        // final float h = (float) bounds.getHeight();
        textScaleFactor = 2.0f / (w * 1.1f);
        //gl.setSwapInterval(0);
    }

    @Override
    public void display(final com.github.opengrabeso.jaagl.GL2 gl) {
        gl.glClear(gl.GL_COLOR_BUFFER_BIT() | gl.GL_DEPTH_BUFFER_BIT());

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(0, 0, 10,
                      0, 0, 0,
                      0, 1, 0);

        renderer.begin3DRendering();
        final Rectangle2D bounds = renderer.getBounds(getText());
        final float w = (float) bounds.getWidth();
        final float h = (float) bounds.getHeight();
        renderer.draw3D(getText(),
                        w / -2.0f * textScaleFactor,
                        h / -2.0f * textScaleFactor,
                        3f,
                        textScaleFactor);

        renderer.end3DRendering();
    }

    @Override
    public void reshape(final com.github.opengrabeso.jaagl.GL2 gl, final int x, final int y, final int width, final int height) {
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(15, (float) width / (float) height, 5, 15);
    }

    @Override
    public void dispose(final com.github.opengrabeso.jaagl.GL2 drawable) {}
}
