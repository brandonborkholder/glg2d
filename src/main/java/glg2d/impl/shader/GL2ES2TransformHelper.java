/**************************************************************************
   Copyright 2012 Brandon Borkholder

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 ***************************************************************************/

package glg2d.impl.shader;

import glg2d.GLG2DTransformHelper;
import glg2d.GLGraphics2D;

import java.awt.RenderingHints.Key;
import java.awt.geom.AffineTransform;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.media.opengl.GL;

public class GL2ES2TransformHelper implements GLG2DTransformHelper {
  protected GLGraphics2D g2d;

  protected Deque<AffineTransform> transformStack = new ArrayDeque<AffineTransform>();

  protected FloatBuffer matrixBuffer = FloatBuffer.allocate(16);

  protected boolean dirtyMatrixBuffer;

  protected int[] viewportDimensions;

  @Override
  public void setG2D(GLGraphics2D g2d) {
    this.g2d = g2d;
    transformStack.clear();
    transformStack.push(new AffineTransform());
    dirtyMatrixBuffer = true;

    viewportDimensions = new int[4];
    GL gl = g2d.getGLContext().getGL();
    gl.glGetIntegerv(GL.GL_VIEWPORT, viewportDimensions, 0);
  }

  @Override
  public void push(GLGraphics2D newG2d) {
    transformStack.push((AffineTransform) getTransform0().clone());
  }

  @Override
  public void pop(GLGraphics2D parentG2d) {
    transformStack.pop();
    dirtyMatrixBuffer = true;
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
    dirtyMatrixBuffer = true;
  }

  @Override
  public void translate(double tx, double ty) {
    getTransform0().translate(tx, ty);
    dirtyMatrixBuffer = true;
  }

  @Override
  public void rotate(double theta) {
    getTransform0().rotate(theta);
    dirtyMatrixBuffer = true;
  }

  @Override
  public void rotate(double theta, double x, double y) {
    getTransform0().rotate(theta, x, y);
    dirtyMatrixBuffer = true;
  }

  @Override
  public void scale(double sx, double sy) {
    getTransform0().scale(sx, sy);
    dirtyMatrixBuffer = true;
  }

  @Override
  public void shear(double shx, double shy) {
    getTransform0().shear(shx, shy);
    dirtyMatrixBuffer = true;
  }

  @Override
  public void transform(AffineTransform Tx) {
    getTransform0().concatenate(Tx);
    dirtyMatrixBuffer = true;
  }

  @Override
  public void setTransform(AffineTransform transform) {
    transformStack.pop();
    transformStack.push(transform);
    dirtyMatrixBuffer = true;
  }

  @Override
  public AffineTransform getTransform() {
    return (AffineTransform) getTransform0().clone();
  }

  protected AffineTransform getTransform0() {
    return transformStack.peek();
  }

  public FloatBuffer getGLMatrixData() {
    return getGLMatrixData(null);
  }

  public FloatBuffer getGLMatrixData(AffineTransform concat) {
    if (concat == null || concat.isIdentity()) {
      if (dirtyMatrixBuffer) {
        updateMatrix(getTransform0(), matrixBuffer);
        dirtyMatrixBuffer = false;
      }
    } else {
      AffineTransform tmp = getTransform();
      tmp.concatenate(concat);
      updateMatrix(tmp, matrixBuffer);
      dirtyMatrixBuffer = true;
    }

    return matrixBuffer;
  }

  protected void updateMatrix(AffineTransform xform, FloatBuffer buffer) {
    // add the GL->G2D coordinate transform and perspective inline here

    int x1 = viewportDimensions[0];
    int y1 = viewportDimensions[1];
    int x2 = viewportDimensions[2];
    int y2 = viewportDimensions[3];

    buffer.rewind();

    buffer.put((float) (2 * xform.getScaleX() / (x2 - x1)));
    buffer.put((float) (-2 * xform.getShearY() / (y2 - y1)));
    buffer.put(0);
    buffer.put(0);

    buffer.put((float) (2 * xform.getShearX() / (x2 - x1)));
    buffer.put((float) (-2 * xform.getScaleY() / (y2 - y1)));
    buffer.put(0);
    buffer.put(0);

    buffer.put(0);
    buffer.put(0);
    buffer.put(-1);
    buffer.put(0);

    buffer.put((float) (2 * xform.getTranslateX() / (x2 - x1) - 1));
    buffer.put((float) (1 - 2 * xform.getTranslateY() / (y2 - y1)));
    buffer.put(0);
    buffer.put(1);

    buffer.flip();
  }
}
