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

/**
 * Fills a simple convex polygon. This class does not test to determine if the
 * polygon is actually simple and convex.
 */
public class FillSimpleConvexPolygonVisitor extends SimplePathVisitor {
  protected GL gl;

  protected VertexBuffer vBuffer = VertexBuffer.getSharedBuffer();

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
    vBuffer.clear();
    assert windingRule == PathIterator.WIND_NON_ZERO : "Invalid assumption";
  }

  @Override
  public void closeLine() {
    vBuffer.drawBuffer(gl, GL.GL_POLYGON);
  }

  @Override
  public void endPoly() {
  }

  @Override
  public void lineTo(float[] vertex) {
    vBuffer.addVertex(vertex[0], vertex[1]);
  }

  @Override
  public void moveTo(float[] vertex) {
    vBuffer.addVertex(vertex[0], vertex[1]);
  }
}
