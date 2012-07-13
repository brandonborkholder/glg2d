/*
 * Copyright 2012 Brandon Borkholder
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
package glg2d.impl.shader;

import glg2d.GLG2DTransformHelper;
import glg2d.GLGraphics2D;
import glg2d.impl.shader.UniformBufferObject.TransformHook;

import java.awt.RenderingHints.Key;
import java.awt.geom.AffineTransform;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.media.opengl.GL;

public class GL2ES2TransformHelper implements GLG2DTransformHelper, TransformHook {
  protected GLGraphics2D g2d;

  protected Deque<AffineTransform> transformStack = new ArrayDeque<AffineTransform>();

  protected float[] glMatrix = new float[16];
  protected boolean dirtyMatrix;

  protected int[] viewportDimensions;

  @Override
  public void setG2D(GLGraphics2D g2d) {
    this.g2d = g2d;
    transformStack.clear();
    transformStack.push(new AffineTransform());
    dirtyMatrix = true;

    viewportDimensions = new int[4];
    GL gl = g2d.getGLContext().getGL();
    gl.glGetIntegerv(GL.GL_VIEWPORT, viewportDimensions, 0);

    if (g2d instanceof GLShaderGraphics2D) {
      ((GLShaderGraphics2D) g2d).getUniformsObject().transformHook = this;
    }
  }

  @Override
  public void push(GLGraphics2D newG2d) {
    transformStack.push((AffineTransform) getTransform0().clone());
  }

  @Override
  public void pop(GLGraphics2D parentG2d) {
    transformStack.pop();
    dirtyMatrix = true;
  }

  @Override
  public void setHint(Key key, Object value) {
    // nop
  }

  @Override
  public void resetHints() {
    // nop
  }

  @Override
  public void dispose() {
  }

  @Override
  public void translate(int x, int y) {
    translate((double) x, (double) y);
    dirtyMatrix = true;
  }

  @Override
  public void translate(double tx, double ty) {
    getTransform0().translate(tx, ty);
    dirtyMatrix = true;
  }

  @Override
  public void rotate(double theta) {
    getTransform0().rotate(theta);
    dirtyMatrix = true;
  }

  @Override
  public void rotate(double theta, double x, double y) {
    getTransform0().rotate(theta, x, y);
    dirtyMatrix = true;
  }

  @Override
  public void scale(double sx, double sy) {
    getTransform0().scale(sx, sy);
    dirtyMatrix = true;
  }

  @Override
  public void shear(double shx, double shy) {
    getTransform0().shear(shx, shy);
    dirtyMatrix = true;
  }

  @Override
  public void transform(AffineTransform Tx) {
    getTransform0().concatenate(Tx);
    dirtyMatrix = true;
  }

  @Override
  public void setTransform(AffineTransform transform) {
    transformStack.pop();
    transformStack.push(transform);
    dirtyMatrix = true;
  }

  @Override
  public AffineTransform getTransform() {
    return (AffineTransform) getTransform0().clone();
  }

  protected AffineTransform getTransform0() {
    return transformStack.peek();
  }

  @Override
  public float[] getGLMatrixData() {
    return getGLMatrixData(null);
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

    int x1 = viewportDimensions[0];
    int y1 = viewportDimensions[1];
    int x2 = viewportDimensions[2];
    int y2 = viewportDimensions[3];

    glMatrix[0] = ((float) (2 * xform.getScaleX() / (x2 - x1)));
    glMatrix[1] = ((float) (-2 * xform.getShearY() / (y2 - y1)));
    // glMatrix[2] = 0;
    // glMatrix[3] = 0;

    glMatrix[4] = ((float) (2 * xform.getShearX() / (x2 - x1)));
    glMatrix[5] = ((float) (-2 * xform.getScaleY() / (y2 - y1)));
    // glMatrix[6] = 0;
    // glMatrix[7] = 0;

    // glMatrix[8] = 0;
    // glMatrix[9] = 0;
    glMatrix[10] = -1;
    // glMatrix[11] = 0;

    glMatrix[12] = ((float) (2 * xform.getTranslateX() / (x2 - x1) - 1));
    glMatrix[13] = ((float) (1 - 2 * xform.getTranslateY() / (y2 - y1)));
    // glMatrix[14] = 0;
    glMatrix[15] = 1;
  }
}
