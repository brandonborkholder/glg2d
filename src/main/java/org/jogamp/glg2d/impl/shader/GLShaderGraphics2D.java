/*
 * Copyright 2013 Brandon Borkholder
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


import javax.media.opengl.GLAutoDrawable;

import org.jogamp.glg2d.GLG2DColorHelper;
import org.jogamp.glg2d.GLG2DImageHelper;
import org.jogamp.glg2d.GLG2DShapeHelper;
import org.jogamp.glg2d.GLG2DTextHelper;
import org.jogamp.glg2d.GLG2DTransformHelper;
import org.jogamp.glg2d.GLGraphics2D;
import org.jogamp.glg2d.impl.shader.text.GL2ES2TextDrawer;

public class GLShaderGraphics2D extends GLGraphics2D {
  protected UniformBufferObject uniforms = new UniformBufferObject();

  public UniformBufferObject getUniformsObject() {
    return uniforms;
  }

  @Override
  protected void setCanvas(GLAutoDrawable drawable) {
    super.setCanvas(drawable);
  }

  @Override
  protected GLG2DImageHelper createImageHelper() {
    return new GL2ES2ImageDrawer();
  }

  @Override
  protected GLG2DColorHelper createColorHelper() {
    return new GL2ES2ColorHelper();
  }

  @Override
  protected GLG2DTransformHelper createTransformHelper() {
    return new GL2ES2TransformHelper();
  }

  @Override
  protected GLG2DShapeHelper createShapeHelper() {
    return new GL2ES2ShapeDrawer();
  }

  @Override
  protected GLG2DTextHelper createTextHelper() {
    return new GL2ES2TextDrawer();
  }
}
