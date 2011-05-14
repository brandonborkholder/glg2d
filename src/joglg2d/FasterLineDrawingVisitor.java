package joglg2d;

import java.awt.BasicStroke;

import javax.media.opengl.GL;

public class FasterLineDrawingVisitor extends SimplePathVisitor {
  protected final GL gl;

  protected float[] lastPoint;

  public FasterLineDrawingVisitor(GL gl, BasicStroke stroke) {
    this.gl = gl;
    gl.glLineWidth(stroke.getLineWidth());
  }

  @Override
  public void beginPoly(int windingRule) {
  }

  @Override
  public void closeLine() {
    gl.glEnd();
  }

  @Override
  public void endPoly() {
    gl.glEnd();
  }

  @Override
  public void lineTo(float[] vertex) {
    gl.glBegin(GL.GL_LINES);
    gl.glVertex2f(lastPoint[0], lastPoint[1]);
    gl.glVertex2f(vertex[0], vertex[1]);
    gl.glEnd();

    gl.glBegin(GL.GL_POINTS);
    gl.glVertex2f(lastPoint[0], lastPoint[1]);
    gl.glVertex2f(vertex[0], vertex[1]);
    gl.glEnd();

    lastPoint = vertex.clone();
  }

  @Override
  public void moveTo(float[] vertex) {
    lastPoint = vertex.clone();
  }
}
