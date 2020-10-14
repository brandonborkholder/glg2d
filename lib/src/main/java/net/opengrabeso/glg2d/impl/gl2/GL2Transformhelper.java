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

import java.awt.geom.AffineTransform;

import com.github.opengrabeso.jaagl.GL;
import com.github.opengrabeso.jaagl.GL2;

import net.opengrabeso.glg2d.GLGraphics2D;
import net.opengrabeso.glg2d.impl.AbstractMatrixHelper;

public class GL2Transformhelper extends AbstractMatrixHelper {
    protected GL2 gl;

    private float[] matrixBuf = new float[16];

    @Override
    public void setG2D(GLGraphics2D g2d) {
        super.setG2D(g2d);
        gl = g2d.getGL().getGL2();

        setupGLView();
        flushTransformToOpenGL();
    }

    protected void setupGLView() {
        int[] viewportDimensions = new int[4];
        gl.glGetIntegerv(gl.GL_VIEWPORT(), viewportDimensions);
        int width = viewportDimensions[2];
        int height = viewportDimensions[3];

        // setup projection
        gl.glMatrixMode(gl.GL_PROJECTION());
        gl.glLoadIdentity();
        gl.glOrtho(0, width, 0, height, -1, 1);

        // the MODELVIEW matrix will get adjusted later

        gl.glMatrixMode(gl.GL_TEXTURE());
        gl.glLoadIdentity();
    }

    /**
     * Sends the {@code AffineTransform} that's on top of the stack to the video
     * card.
     */
    protected void flushTransformToOpenGL() {
        float[] matrix = getGLMatrix(stack.peek());

        gl.glMatrixMode(gl.GL_MODELVIEW());
        gl.glLoadMatrixf(matrix, 0);
    }

    /**
     * Gets the GL matrix for the {@code AffineTransform} with the change of
     * coordinates inlined. Since Java2D uses the upper-left as 0,0 and OpenGL
     * uses the lower-left as 0,0, we have to pre-multiply the matrix before
     * loading it onto the video card.
     */
    protected float[] getGLMatrix(AffineTransform transform) {
        matrixBuf[0] = (float) transform.getScaleX();
        matrixBuf[1] = -(float) transform.getShearY();
        matrixBuf[4] = (float) transform.getShearX();
        matrixBuf[5] = -(float) transform.getScaleY();
        matrixBuf[10] = 1;
        matrixBuf[12] = (float) transform.getTranslateX();
        matrixBuf[13] = g2d.getCanvasHeight() - (float) transform.getTranslateY();
        matrixBuf[15] = 1;

        return matrixBuf;
    }
}
