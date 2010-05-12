package joglg2d;

import java.awt.BasicStroke;
import java.nio.DoubleBuffer;

import javax.media.opengl.GL;

/**
 * @author borkholder
 * @created May 11, 2010
 *
 */
public class FastLineDrawingVisitor implements VertexVisitor {
  protected final GL gl;

  protected final BasicStroke stroke;

  protected DoubleBuffer buffer = DoubleBuffer.allocate(500);

  protected double[] lastPoint;

  public FastLineDrawingVisitor(GL gl, BasicStroke stroke) {
    this.gl = gl;
    this.stroke = stroke;
  }

  @Override
  public void beginPoly(int windingRule) {
    buffer.rewind();
  }

  @Override
  public void closeLine() {
    double[] tmp = new double[4];
    int pos = buffer.position();
    buffer.rewind();
    buffer.get(tmp);
    buffer.position(pos);
    buffer.put(tmp);
    renderBuffer(GL.GL_QUAD_STRIP);
  }

  @Override
  public void endPoly() {
    renderBuffer(GL.GL_QUAD_STRIP);
  }

  @Override
  public void lineTo(double[] vertex) {
    double offset = stroke.getLineWidth() / 2;
    double[] thisPoint = new double[2];
    if (vertex[0] == lastPoint[0]) {
      thisPoint[0] = lastPoint[0] - offset;
      thisPoint[1] = lastPoint[1];
      buffer.put(thisPoint);
      thisPoint[0] = lastPoint[0] + offset;
      thisPoint[1] = lastPoint[1];
      buffer.put(thisPoint);

      thisPoint[0] = lastPoint[0] - offset;
      thisPoint[1] = vertex[1];
      buffer.put(thisPoint);
      thisPoint[0] = lastPoint[0] + offset;
      thisPoint[1] = vertex[1];
      buffer.put(thisPoint);
    } else {
      double angle = Math.atan((vertex[1] - lastPoint[1]) / (vertex[0] - lastPoint[0]));
      double sin = Math.sin(angle) * offset;
      double cos = Math.cos(angle) * offset;
      thisPoint[0] = lastPoint[0] + sin;
      thisPoint[1] = lastPoint[1] - cos;
      buffer.put(thisPoint);
      thisPoint[0] = lastPoint[0] - sin;
      thisPoint[1] = lastPoint[1] + cos;
      buffer.put(thisPoint);

      thisPoint[0] = vertex[0] + sin;
      thisPoint[1] = vertex[1] - cos;
      buffer.put(thisPoint);
      thisPoint[0] = vertex[0] - sin;
      thisPoint[1] = vertex[1] + cos;
      buffer.put(thisPoint);
    }

    lastPoint = vertex;
  }

  @Override
  public void moveTo(double[] vertex) {
    lastPoint = vertex;
    renderBuffer(GL.GL_QUAD_STRIP);
  }

  protected void renderBuffer(int mode) {
    if (buffer.position() == 0) {
      return;
    }

    buffer.limit(buffer.position());
    buffer.rewind();
    gl.glBegin(mode);
    while (buffer.hasRemaining()) {
      gl.glVertex2d(buffer.get(), buffer.get());
    }
    gl.glEnd();

    buffer.rewind();
    buffer.limit(buffer.capacity());
  }
}
