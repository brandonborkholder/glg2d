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
package glg2d.impl.gl2;

import glg2d.SimplePathVisitor;

import java.awt.BasicStroke;
import java.awt.geom.PathIterator;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
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
  protected GL2 gl;

  protected GLUtessellator tesselator;

  protected GLUtessellatorCallback callback;

  protected boolean contourClosed = true;

  public TesselatorVisitor() {
    callback = new TessellatorCallback();
  }

  @Override
  public void setGLContext(GL context) {
    gl = context.getGL2();
  }

  @Override
  public void setStroke(BasicStroke stroke) {
    // nop
  }

  @Override
  public void beginPoly(int windingRule) {
    gl.glPushAttrib(GL2.GL_ENABLE_BIT);
    gl.glDisable(GL2.GL_POLYGON_SMOOTH);
    tesselator = GLU.gluNewTess();

    switch (windingRule) {
    case PathIterator.WIND_EVEN_ODD:
      GLU.gluTessProperty(tesselator, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_ODD);
      break;

    case PathIterator.WIND_NON_ZERO:
      GLU.gluTessProperty(tesselator, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_NONZERO);
      break;
    }

    GLU.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, callback);
    GLU.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, callback);
    GLU.gluTessCallback(tesselator, GLU.GLU_TESS_END, callback);
    GLU.gluTessCallback(tesselator, GLU.GLU_TESS_ERROR, callback);
    GLU.gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE, callback);
    GLU.gluTessNormal(tesselator, 0, 0, -1);

    GLU.gluTessBeginPolygon(tesselator, null);
  }

  @Override
  public void lineTo(float[] vertex) {
    double[] v = new double[] { vertex[0], vertex[1], 0 };
    GLU.gluTessVertex(tesselator, v, 0, v);
  }

  @Override
  public void moveTo(float[] vertex) {
    GLU.gluTessBeginContour(tesselator);
    double[] v = new double[] { vertex[0], vertex[1], 0 };
    GLU.gluTessVertex(tesselator, v, 0, v);
    contourClosed = false;
  }

  @Override
  public void closeLine() {
    GLU.gluTessEndContour(tesselator);
    contourClosed = true;
  }

  @Override
  public void endPoly() {
    // shapes may just end on the starting point without calling closeLine
    if (!contourClosed) {
      closeLine();
    }

    GLU.gluTessEndPolygon(tesselator);
    GLU.gluDeleteTess(tesselator);
    gl.glPopAttrib();
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
      throw new GLException("Tesselation Error: " + new GLU().gluErrorString(errnum));
    }
  }
}
