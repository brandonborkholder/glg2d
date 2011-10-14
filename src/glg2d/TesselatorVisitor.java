/**************************************************************************
   Copyright 2011 Brandon Borkholder

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

package glg2d;

import java.awt.BasicStroke;
import java.awt.geom.PathIterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallback;
import javax.media.opengl.glu.GLUtessellatorCallbackAdapter;

/**
 * Fills a shape by tesselating it with the GLU library. This is a slower
 * implementation and {@code FillNonintersectingPolygonVisitor} should be used
 * when possible.
 */
public class TesselatorVisitor extends SimplePathVisitor {
  protected GLU glu;

  protected GL gl;

  protected GLUtessellator tesselator;

  protected GLUtessellatorCallback callback;

  public TesselatorVisitor() {
    glu = new GLU();
    callback = new TessellatorCallback();
  }

  @Override
  public void setGLContext(GL context) {
    gl = context;
  }
  
  @Override
  public void setStroke(BasicStroke stroke) {
    // nop
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

    glu.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, callback);
    glu.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, callback);
    glu.gluTessCallback(tesselator, GLU.GLU_TESS_END, callback);
    glu.gluTessCallback(tesselator, GLU.GLU_TESS_ERROR, callback);
    glu.gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE, callback);
    glu.gluTessNormal(tesselator, 0, 0, -1);

    glu.gluTessBeginPolygon(tesselator, null);
  }

  @Override
  public void lineTo(float[] vertex) {
    double[] v = new double[] { vertex[0], vertex[1], 0 };
    glu.gluTessVertex(tesselator, v, 0, v);
  }

  @Override
  public void moveTo(float[] vertex) {
    glu.gluTessBeginContour(tesselator);
    double[] v = new double[] { vertex[0], vertex[1], 0 };
    glu.gluTessVertex(tesselator, v, 0, v);
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

  protected class TessellatorCallback extends GLUtessellatorCallbackAdapter {
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
      throw new GLException("Tesselation Error: " + glu.gluErrorString(errnum));
    }
  }
}
