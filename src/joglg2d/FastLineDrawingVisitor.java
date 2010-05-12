package joglg2d;

import java.nio.DoubleBuffer;

import javax.media.opengl.GL;

/**
 * @author borkholder
 * @created May 11, 2010
 *
 */
public class FastLineDrawingVisitor implements VertexVisitor {
  protected final GL gl;

  protected DoubleBuffer buffer = DoubleBuffer.allocate(100);

  protected boolean drawVertices;

  public FastLineDrawingVisitor(GL gl) {
    this.gl = gl;
  }

  public void setDrawVertices(boolean draw) {
    drawVertices = draw;
  }

  @Override
  public void beginPoly(int windingRule) {
    buffer.rewind();
  }

  @Override
  public void closeLine() {
    renderBuffer(GL.GL_LINE_LOOP);
  }

  @Override
  public void endPoly() {
    renderBuffer(GL.GL_LINE_STRIP);
  }

  @Override
  public void lineTo(double[] vertex) {
    buffer.put(vertex, 0, 2);
  }

  @Override
  public void moveTo(double[] vertex) {
    renderBuffer(GL.GL_LINE_STRIP);
    buffer.put(vertex, 0, 2);
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

//    if (drawVertices) {
//      buffer.rewind();
//      gl.glPointSize(1);
//      gl.glBegin(GL.GL_POINTS);
//      while (buffer.hasRemaining()) {
//        gl.glVertex2d(buffer.get(), buffer.get());
//      }
//      gl.glEnd();
//    }

    buffer.rewind();
    buffer.limit(buffer.capacity());
  }
}
