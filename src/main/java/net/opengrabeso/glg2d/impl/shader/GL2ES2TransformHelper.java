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

import java.awt.geom.AffineTransform;

import com.github.opengrabeso.jaagl.GL;

import net.opengrabeso.glg2d.GLGraphics2D;
import net.opengrabeso.glg2d.impl.AbstractMatrixHelper;
import net.opengrabeso.glg2d.impl.shader.UniformBufferObject.TransformHook;

public class GL2ES2TransformHelper extends AbstractMatrixHelper implements TransformHook {
  protected float[] glMatrix;
  protected boolean dirtyMatrix;

  protected int[] viewportDimensions;

  @Override
  public void setG2D(GLGraphics2D g2d) {
    super.setG2D(g2d);

    dirtyMatrix = true;
    glMatrix = new float[16];
    viewportDimensions = new int[4];

    GL gl = g2d.getGL();
    gl.glGetIntegerv(gl.GL_VIEWPORT(), viewportDimensions, 0);

    if (g2d instanceof GLShaderGraphics2D) {
      ((GLShaderGraphics2D) g2d).getUniformsObject().transformHook = this;
    } else {
      throw new IllegalArgumentException(GLGraphics2D.class.getName() + " implementation must be instance of "
          + GLShaderGraphics2D.class.getSimpleName());
    }
  }

  @Override
  public float[] getGLMatrixData() {
    return getGLMatrixData(null);
  }

  @Override
  protected void flushTransformToOpenGL() {
    // only set dirty, we'll update lazily
    dirtyMatrix = true;
  }

  @Override
  public float[] getGLMatrixData(AffineTransform concat) {
    if (concat == null || concat.isIdentity()) {
      if (dirtyMatrix) {
        updateGLMatrix(getTransform0());
        dirtyMatrix = false;
      }
    } else {
      AffineTransform tmp = getTransform();
      tmp.concatenate(concat);
      updateGLMatrix(tmp);
      dirtyMatrix = true;
    }

    return glMatrix;
  }

  protected void updateGLMatrix(AffineTransform xform) {
    // add the GL->G2D coordinate transform and perspective inline here

    // Note this isn't quite the same as the GL2 implementation because GL2 has
    // an orthographic projection matrix

    float x1 = viewportDimensions[0];
    float y1 = viewportDimensions[1];
    float x2 = viewportDimensions[2];
    float y2 = viewportDimensions[3];

    float invWidth = 1f / (x2 - x1);
    float invHeight = 1f / (y2 - y1);
    
    glMatrix[0] = ((float) (2 * xform.getScaleX() * invWidth));
    glMatrix[1] = ((float) (-2 * xform.getShearY() * invHeight));
    // glMatrix[2] = 0;
    // glMatrix[3] = 0;

    glMatrix[4] = ((float) (2 * xform.getShearX() * invWidth));
    glMatrix[5] = ((float) (-2 * xform.getScaleY() * invHeight));
    // glMatrix[6] = 0;
    // glMatrix[7] = 0;

    // glMatrix[8] = 0;
    // glMatrix[9] = 0;
    glMatrix[10] = -1;
    // glMatrix[11] = 0;

    glMatrix[12] = ((float) (2 * xform.getTranslateX() * invWidth - 1));
    glMatrix[13] = ((float) (1 - 2 * xform.getTranslateY() * invHeight));
    // glMatrix[14] = 0;
    glMatrix[15] = 1;
  }
}
