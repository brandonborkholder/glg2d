/*
 * Copyright 2015 Brandon Borkholder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.opengrabeso.glg2d.impl.gl2;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;

import com.github.opengrabeso.jaagl.GL2;
import com.github.opengrabeso.jaagl.GL2GL3;

import net.opengrabeso.glg2d.impl.GLG2DNotImplemented;
import net.opengrabeso.glg2d.GLGraphics2D;
import net.opengrabeso.glg2d.impl.AbstractColorHelper;

public class GL2ColorHelper extends AbstractColorHelper {
    protected GL2 gl;

    @Override
    public void setG2D(GLGraphics2D g2d) {
        super.setG2D(g2d);
        gl = g2d.getGL().getGL2();
    }

    @Override
    public void setPaint(Paint paint) {
        if (paint instanceof Color) {
            setColor((Color) paint);
        } else if (paint instanceof GradientPaint) {
            setColor(((GradientPaint) paint).getColor1());
            GLG2DNotImplemented.notImplemented("setPaint(Paint) with GradientPaint");
        } else if (paint instanceof MultipleGradientPaint) {
            setColor(((MultipleGradientPaint) paint).getColors()[0]);
            GLG2DNotImplemented.notImplemented("setPaint(Paint) with MultipleGradientPaint");
        } else {
            GLG2DNotImplemented.notImplemented("setPaint(Paint) with " + paint.getClass().getSimpleName());
            // This will probably be easier to handle with a fragment shader
            // in the shader pipeline, not sure how to handle it in the fixed-
            // function pipeline.
        }
    }

    @Override
    public Paint getPaint() {
        return getColor();
    }

    @Override
    public void setColorNoRespectComposite(Color c) {
        setColor(gl, c, 1);
    }

    /**
     * Sets the current color with a call to glColor4*. But it respects the
     * AlphaComposite if any. If the AlphaComposite wants to pre-multiply an
     * alpha, pre-multiply it.
     */
    @Override
    public void setColorRespectComposite(Color c) {
        float alpha = 1;
        Composite composite = getComposite();
        if (composite instanceof AlphaComposite) {
            alpha = ((AlphaComposite) composite).getAlpha();
        }

        setColor(gl, c, alpha);
    }

    private void setColor(GL2 gl, Color c, float preMultiplyAlpha) {
        int rgb = c.getRGB();
        gl.glColor4ub((byte) (rgb >> 16 & 0xFF), (byte) (rgb >> 8 & 0xFF), (byte) (rgb & 0xFF), (byte) ((rgb >> 24 & 0xFF) * preMultiplyAlpha));
    }

    @Override
    public void setPaintMode() {
        GLG2DNotImplemented.notImplemented("setPaintMode()");
        // TODO Auto-generated method stub
    }

    @Override
    public void setXORMode(Color c) {
        GLG2DNotImplemented.notImplemented("setXORMode(Color)");
        // TODO Auto-generated method stub
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        // glRasterPos* is transformed, but CopyPixels is not
        int x2 = x + dx;
        int y2 = y + dy + height;
        gl.glRasterPos2i(x2, y2);

        int x1 = x;
        int y1 = g2d.getCanvasHeight() - (y + height);
        gl.glCopyPixels(x1, y1, width, height, gl.GL_COLOR());
    }
}
