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
package net.opengrabeso.glg2d.impl.shader;


import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.nio.FloatBuffer;

import com.github.opengrabeso.jaagl.GL;
import com.github.opengrabeso.jaagl.GL2GL3;

import com.github.opengrabeso.jaagl.jogl.JoGL;
import net.opengrabeso.opengl.util.texture.Texture;
import net.opengrabeso.glg2d.GLGraphics2D;
import net.opengrabeso.glg2d.impl.AbstractImageHelper;

import com.jogamp.common.nio.Buffers;

public class GL2ES2ImageDrawer extends AbstractImageHelper {
    protected GLShaderGraphics2D g2d;
    protected GL2GL3 gl;

    protected FloatBuffer vertTexCoords = Buffers.newDirectFloatBuffer(16);
    protected GL2ES2ImagePipeline shader;

    private float[] white = new float[]{1, 1, 1, 1};

    public GL2ES2ImageDrawer() {
        this(new GL2ES2ImagePipeline());
    }

    public GL2ES2ImageDrawer(GL2ES2ImagePipeline shader) {
        this.shader = shader;
    }

    @Override
    public void setG2D(GLGraphics2D g2d) {
        super.setG2D(g2d);

        if (g2d instanceof GLShaderGraphics2D) {
            this.g2d = (GLShaderGraphics2D) g2d;
        } else {
            throw new IllegalArgumentException(GLGraphics2D.class.getName() + " implementation must be instance of "
                    + GLShaderGraphics2D.class.getSimpleName());
        }

        gl = g2d.getGL().getGL2GL3();
        if (!shader.isSetup()) {
            shader.setup(gl);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        shader.delete(gl);
    }

    @Override
    protected void begin(Texture texture, AffineTransform xform, Color bgcolor) {
        /*
         * FIXME This is unexpected since we never disable blending, but in some
         * cases it interacts poorly with multiple split panes, scroll panes and the
         * text renderer to disable blending.
         */
        g2d.setComposite(g2d.getComposite());

        gl.glTexParameteri(gl.GL_TEXTURE_2D(), gl.GL_TEXTURE_MIN_FILTER(), gl.GL_NEAREST());
        gl.glTexParameteri(gl.GL_TEXTURE_2D(), gl.GL_TEXTURE_MAG_FILTER(), gl.GL_NEAREST());

        gl.glActiveTexture(gl.GL_TEXTURE0());
        texture.enable(gl);
        texture.bind(gl);

        shader.use(gl, true);

        if (bgcolor == null) {
            white[3] = g2d.getUniformsObject().colorHook.getAlpha();
            shader.setColor(gl, white);
        } else {
            float[] rgba = g2d.getUniformsObject().colorHook.getRGBA();
            shader.setColor(gl, rgba);
        }

        if (xform == null) {
            shader.setTransform(gl, g2d.getUniformsObject().transformHook.getGLMatrixData());
        } else {
            shader.setTransform(gl, g2d.getUniformsObject().transformHook.getGLMatrixData(xform));
        }

        shader.setTextureUnit(gl, 0);
    }

    @Override
    protected void applyTexture(Texture texture, int dx1, int dy1, int dx2, int dy2, float sx1, float sy1, float sx2, float sy2) {
        vertTexCoords.rewind();

        // interleave vertex and texture coordinates
        vertTexCoords.put(dx1);
        vertTexCoords.put(dy1);
        vertTexCoords.put(sx1);
        vertTexCoords.put(sy1);

        vertTexCoords.put(dx1);
        vertTexCoords.put(dy2);
        vertTexCoords.put(sx1);
        vertTexCoords.put(sy2);

        vertTexCoords.put(dx2);
        vertTexCoords.put(dy1);
        vertTexCoords.put(sx2);
        vertTexCoords.put(sy1);

        vertTexCoords.put(dx2);
        vertTexCoords.put(dy2);
        vertTexCoords.put(sx2);
        vertTexCoords.put(sy2);

        vertTexCoords.flip();
        shader.draw(gl, vertTexCoords);
    }

    @Override
    protected void end(Texture texture) {
        shader.use(gl, false);
        texture.disable(gl);
    }
}
