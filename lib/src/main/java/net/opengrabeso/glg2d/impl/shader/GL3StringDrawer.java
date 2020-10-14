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

import com.github.opengrabeso.jaagl.GL2GL3;
import net.opengrabeso.opengl.util.awt.TextRenderer;
import net.opengrabeso.glg2d.impl.gl2.GL2StringDrawer;

import java.awt.*;

/**
 * Modification of {{GLGraphics2D}} class so that is can be used with {@code GLShaderGraphics2D}
 */
public class GL3StringDrawer extends GL2StringDrawer {

    private final GL2GL3 gl;

    public GL3StringDrawer(GL2GL3 gl) {
        this.gl = gl;
    }

    @Override
    protected TextRenderer createTextRenderer(Font font, boolean antialias) {
        return new TextRenderer(gl, font, antialias, false);
    }

    @Override
    protected boolean useVerticalFlip() {
        return true;
    }

    @Override
    protected float[] getTransform(TextRenderer renderer) {
        setTextColorRespectComposite(renderer);

        float[] matrix = ((GLShaderGraphics2D) g2d).getUniformsObject().transformHook.getGLMatrixData();
        return matrix;
    }

    @Override
    protected void cleanupMatrix(TextRenderer renderer) {
    }
}
