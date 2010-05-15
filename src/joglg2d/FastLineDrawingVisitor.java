package joglg2d;

import java.awt.BasicStroke;

import javax.media.opengl.GL;

/**
 * @author borkholder
 * @created May 11, 2010
 *
 */
public class FastLineDrawingVisitor implements VertexVisitor {
  protected final GL gl;

  protected final BasicStroke stroke;

  protected double[] lastPoint;

  protected double[] firstTwoPoints;

  public FastLineDrawingVisitor(GL gl, BasicStroke stroke) {
    this.gl = gl;
    this.stroke = stroke;
  }

  @Override
  public void beginPoly(int windingRule) {
  }

  @Override
  public void closeLine() {
    gl.glVertex2d(firstTwoPoints[0], firstTwoPoints[1]);
    gl.glVertex2d(firstTwoPoints[2], firstTwoPoints[3]);
    gl.glEnd();
  }

  @Override
  public void endPoly() {
    gl.glEnd();
  }

  @Override
  public void lineTo(double[] vertex) {
    double offset = stroke.getLineWidth() / 2;
    double x, y;
    if (vertex[0] == lastPoint[0]) {
      x = lastPoint[0] - offset;
      y = lastPoint[1];
      gl.glVertex2d(x, y);
      x = lastPoint[0] + offset;
      y = lastPoint[1];
      gl.glVertex2d(x, y);

      if (firstTwoPoints == null) {
        firstTwoPoints = new double[4];
        firstTwoPoints[0] = lastPoint[0] - offset;
        firstTwoPoints[1] = lastPoint[1];
        firstTwoPoints[2] = lastPoint[0] + offset;
        firstTwoPoints[3] = lastPoint[1];
      }

      x = lastPoint[0] - offset;
      y = vertex[1];
      gl.glVertex2d(x, y);
      x = lastPoint[0] + offset;
      y = vertex[1];
      gl.glVertex2d(x, y);
    } else {
      double angle = Math.atan((vertex[1] - lastPoint[1]) / (vertex[0] - lastPoint[0]));
      double sin = Math.sin(angle) * offset;
      double cos = Math.cos(angle) * offset;
      x = lastPoint[0] + sin;
      y = lastPoint[1] - cos;
      gl.glVertex2d(x, y);
      x = lastPoint[0] - sin;
      y = lastPoint[1] + cos;
      gl.glVertex2d(x, y);

      if (firstTwoPoints == null) {
        firstTwoPoints = new double[4];
        firstTwoPoints[0] = lastPoint[0] + sin;
        firstTwoPoints[1] = lastPoint[1] - cos;
        firstTwoPoints[2] = lastPoint[0] - sin;
        firstTwoPoints[3] = lastPoint[1] + cos;
      }

      x = vertex[0] + sin;
      y = vertex[1] - cos;
      gl.glVertex2d(x, y);
      x = vertex[0] - sin;
      y = vertex[1] + cos;
      gl.glVertex2d(x, y);
    }

    lastPoint = vertex;
  }

  @Override
  public void moveTo(double[] vertex) {
    gl.glEnd();
    gl.glBegin(GL.GL_QUAD_STRIP);
    lastPoint = vertex;
    firstTwoPoints = null;
  }
}
