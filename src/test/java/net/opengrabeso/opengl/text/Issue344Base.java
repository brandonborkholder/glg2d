package net.opengrabeso.opengl.text;

import java.awt.Font;
import java.awt.geom.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import net.opengrabeso.opengl.Jaagl2EventListener;
import net.opengrabeso.opengl.SelectJaaglEventListener;
import net.opengrabeso.opengl.util.awt.TextRenderer;


/** Test Code adapted from TextCube.java (in JOGL demos)
 *
 * @author spiraljetty
 * @author kbr
 */

public abstract class Issue344Base implements Jaagl2EventListener
{
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
        gl.glClearColor(0.8f, 0.5f, 0.5f, 1);
        gl.glClear(gl.GL_COLOR_BUFFER_BIT() | gl.GL_DEPTH_BUFFER_BIT());

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -10);

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

        //void gluPerspective(	GLdouble fovy, GLdouble aspect, GLdouble zNear, GLdouble zFar);
        // glu.gluPerspective(15, (float) width / (float) height, 5, 15);


        float fov = 15;
        float aspect = (float) width / (float) height;
        float znear = 5;
        float zfar = 15;

        float[] m = new float[16];
        float f = 1 / (float) Math.tan(fov * Math.PI / 360);

        m[0] = f / aspect;
        m[1] = 0;
        m[2] = 0;
        m[3] = 0;

        m[4] = 0;
        m[5] = f;
        m[6] = 0;
        m[7] = 0;

        m[8] = 0;
        m[9] = 0;
        m[10] = (zfar + znear) / (znear - zfar);
        m[11] = -1;

        m[12] = 0;
        m[13] = 0;
        m[14] = 2 * zfar * znear / (znear - zfar);
        m[15] = 0;

        gl.glLoadMatrixf(m, 0);
    }

    @Override
    public void dispose(final com.github.opengrabeso.jaagl.GL2 drawable) {}
}
