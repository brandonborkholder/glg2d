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

  @Override
  public void setG2D(GLGraphics2D g2d) {
    this.g2d = g2d;
    transformStack.clear();
    transformStack.push(new AffineTransform());
    dirtyMatrixBuffer = true;
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
    // we're going to add the GL->G2D coordinate transform inline here
    int[] viewportDimensions = new int[4];
    GL gl = g2d.getGLContext().getGL();
    gl.glGetIntegerv(GL.GL_VIEWPORT, viewportDimensions, 0);
    int canvasWidth = viewportDimensions[2];
    int canvasHeight = viewportDimensions[3];

    float[] matrix = new float[16];

    matrix[0] = (float) (2 * xform.getScaleX() / canvasWidth);
    matrix[1] = (float) (-2 * xform.getShearY() / canvasHeight);
    matrix[4] = (float) (2 * xform.getShearX() / canvasWidth);
    matrix[5] = (float) (-2 * xform.getScaleY() / canvasHeight);
    matrix[10] = -1;
    matrix[12] = (float) (2 * xform.getTranslateX() / canvasWidth - 1);
    matrix[13] = (float) (1 - 2 * xform.getTranslateY() / canvasHeight);
    matrix[15] = 1;

    buffer.rewind();
    buffer.put(matrix);
    buffer.flip();
  }
}
