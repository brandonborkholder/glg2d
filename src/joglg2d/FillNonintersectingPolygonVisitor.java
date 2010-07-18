/**************************************************************************
   Copyright 2010 Brandon Borkholder

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

package joglg2d;

import java.awt.geom.PathIterator;

import javax.media.opengl.GL;

/**
 * @author borkholder
 * @created May 11, 2010
 */
public class FillNonintersectingPolygonVisitor extends SimplePathVisitor {
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
  public void lineTo(float[] vertex) {
    gl.glVertex2f(vertex[0], vertex[1]);
  }

  @Override
  public void moveTo(float[] vertex) {
    gl.glBegin(GL.GL_POLYGON);
    gl.glVertex2f(vertex[0], vertex[1]);
  }
}
