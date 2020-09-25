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
package org.jogamp.glg2d.impl.shader;

import com.github.opengrabeso.jaagl.GL3;
import com.github.opengrabeso.ogltext.util.awt.TextRenderer;
import org.jogamp.glg2d.impl.gl2.GL2StringDrawer;

import java.awt.*;

/**
 * Modification of {{GLGraphics2D}} class so that is can be used with {@code GLShaderGraphics2D}
 */
public class GL3StringDrawer extends GL2StringDrawer {

    private final GL3 gl;

    public GL3StringDrawer(GL3 gl) {
        this.gl = gl;
    }

    @Override
    protected TextRenderer createTextRenderer(Font font, boolean antialias) {
        return new TextRenderer(font, antialias, false, gl);
    }

    @Override
    protected boolean useVerticalFlip() {return true;}

    @Override
    protected void setupMatrix(TextRenderer renderer) {
        setTextColorRespectComposite(renderer);

        float[] matrix = ((GLShaderGraphics2D) g2d).getUniformsObject().transformHook.getGLMatrixData();
        renderer.setTransform(gl, matrix);
    }

    @Override
    protected void cleanupMatrix(TextRenderer renderer) {
    }
}
