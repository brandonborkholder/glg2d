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
import net.opengrabeso.glg2d.GLG2DColorHelper;
import net.opengrabeso.glg2d.GLG2DImageHelper;
import net.opengrabeso.glg2d.GLG2DShapeHelper;
import net.opengrabeso.glg2d.GLG2DTextHelper;
import net.opengrabeso.glg2d.GLG2DTransformHelper;
import net.opengrabeso.glg2d.GLGraphics2D;

public class GLShaderGraphics2D extends GLGraphics2D {
    protected UniformBufferObject uniforms = new UniformBufferObject();

    public GLShaderGraphics2D(GL2GL3 gl) {
        super(gl);
    }

    public UniformBufferObject getUniformsObject() {
        return uniforms;
    }

    private String shaderDirectory() {
        return gl.isGL3() ? "gl3/" : "gl2/";
    }

    @Override
    protected GLG2DImageHelper createImageHelper() {
        return new GL2ES2ImageDrawer(shaderDirectory());
    }

    @Override
    protected GLG2DColorHelper createColorHelper() {
        return new GL2ES2ColorHelper(shaderDirectory());
    }

    @Override
    protected GLG2DTransformHelper createTransformHelper() {
        return new GL2ES2TransformHelper();
    }

    @Override
    protected GLG2DShapeHelper createShapeHelper() {
        return new GL2ES2ShapeDrawer(shaderDirectory());
    }

    @Override
    protected GLG2DTextHelper createTextHelper() {
        return new GL3StringDrawer(gl);
    }
}
