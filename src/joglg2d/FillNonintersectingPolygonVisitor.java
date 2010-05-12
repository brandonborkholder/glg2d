package joglg2d;

import java.awt.geom.PathIterator;

import javax.media.opengl.GL;

/**
 * @author borkholder
 * @created May 11, 2010
 */
public class FillNonintersectingPolygonVisitor implements VertexVisitor {
  protected final GL gl;

  public FillNonintersectingPolygonVisitor(GL gl) {
    this.gl = gl;
  }

  @Override
  public void beginPoly(int windingRule) {
    assert windingRule == PathIterator.WIND_NON_ZERO : "Invalid assumption";
  }

  @Override
  public void closeLine() {
    gl.glEnd();
  }

  @Override
  public void endPoly() {
  }

  @Override
  public void lineTo(double[] vertex) {
    gl.glVertex2d(vertex[0], vertex[1]);
  }

  @Override
  public void moveTo(double[] vertex) {
    gl.glBegin(GL.GL_POLYGON);
    gl.glVertex2d(vertex[0], vertex[1]);
  }
}
