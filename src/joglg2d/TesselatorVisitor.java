package joglg2d;

import java.awt.geom.PathIterator;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallbackAdapter;

/**
 * @author borkholder
 * @created May 11, 2010
 *
 */
public class TesselatorVisitor extends GLUtessellatorCallbackAdapter implements VertexVisitor {
  protected final GLU glu;

  protected final GL gl;

  protected GLUtessellator tesselator;

  public TesselatorVisitor(GL gl, GLU glu) {
    this.gl = gl;
    this.glu = glu;
  }

  @Override
  public void beginPoly(int windingRule) {
    tesselator = glu.gluNewTess();

    switch (windingRule) {
    case PathIterator.WIND_EVEN_ODD:
      glu.gluTessProperty(tesselator, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_ODD);
      break;

    case PathIterator.WIND_NON_ZERO:
      glu.gluTessProperty(tesselator, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_NONZERO);
      break;
    }

    glu.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, this);
    glu.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, this);
    glu.gluTessCallback(tesselator, GLU.GLU_TESS_END, this);
    glu.gluTessCallback(tesselator, GLU.GLU_TESS_ERROR, this);
    glu.gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE, this);
    glu.gluTessNormal(tesselator, 0, 0, -1);

    glu.gluTessBeginPolygon(tesselator, null);
  }

  @Override
  public void lineTo(double[] vertex) {
    glu.gluTessVertex(tesselator, vertex, 0, vertex);
  }

  @Override
  public void moveTo(double[] vertex) {
    glu.gluTessBeginContour(tesselator);
    glu.gluTessVertex(tesselator, vertex, 0, vertex);
  }

  @Override
  public void closeLine() {
    glu.gluTessEndContour(tesselator);
  }

  @Override
  public void endPoly() {
    glu.gluTessEndPolygon(tesselator);
    glu.gluDeleteTess(tesselator);
  }

  @Override
  public void begin(int type) {
    gl.glBegin(type);
  }

  @Override
  public void end() {
    gl.glEnd();
  }

  @Override
  public void vertex(Object vertexData) {
    assert vertexData instanceof double[] : "Invalid assumption";
    double[] v = (double[]) vertexData;
    gl.glVertex2d(v[0], v[1]);
  }

  @Override
  public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
    outData[0] = coords;
  }

  @Override
  public void error(int errnum) {
    System.err.println("Tessellation Error: " + glu.gluErrorString(errnum));
  }
}
