package net.opengrabeso.glg2d.impl.gl2;

import com.github.opengrabeso.jaagl.GL2;
import com.github.opengrabeso.ogltext.util.awt.TextRenderer;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;

import java.awt.*;

public class GL2StringDrawerImpl extends GL2StringDrawer{

    private final GL2 gl;

    public GL2StringDrawerImpl(GL2 gl) {
        this.gl = gl;
    }

    @Override
    protected TextRenderer createTextRenderer(Font font, boolean antialias) {
        return new TextRenderer(font, antialias, false, gl);
    }

    @Override
    protected void setupMatrix(TextRenderer renderer) {
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glScalef(1, -1, 1);
        gl.glTranslatef(0, -g2d.getCanvasHeight(), 0);
    }

    @Override
    protected boolean useVerticalFlip() {return false;}

    @Override
    protected void cleanupMatrix(TextRenderer renderer) {
        gl.glPopMatrix();
    }

}
