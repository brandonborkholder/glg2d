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

  protected double[] secondLastPoint;

  protected double[] firstPoint;

  protected double[] secondPoint;

  protected boolean cappedLine;

  public FastLineDrawingVisitor(GL gl, BasicStroke stroke) {
    this.gl = gl;
    this.stroke = stroke;
  }

  @Override
  public void beginPoly(int windingRule) {
  }

  @Override
  public void closeLine() {
    lineTo(firstPoint);
    firstPoint = lastPoint = null;
    gl.glEnd();
  }

  @Override
  public void endPoly() {
    gl.glEnd();

    if (firstPoint != null) {
      applyEndCap(firstPoint, secondPoint);
      applyEndCap(lastPoint, secondLastPoint);
    }
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

      x = vertex[0] + sin;
      y = vertex[1] - cos;
      gl.glVertex2d(x, y);
      x = vertex[0] - sin;
      y = vertex[1] + cos;
      gl.glVertex2d(x, y);
    }

    secondLastPoint = lastPoint;
    lastPoint = vertex;
    if (secondPoint == null) {
      secondPoint = vertex;
    }
  }

  @Override
  public void moveTo(double[] vertex) {
    gl.glEnd();

    if (firstPoint != null) {
      applyEndCap(lastPoint, secondLastPoint);
      applyEndCap(firstPoint, secondPoint);
    }

    gl.glBegin(GL.GL_QUAD_STRIP);
    lastPoint = vertex;
    secondLastPoint = null;
    firstPoint = vertex;
    secondPoint = null;
  }

  protected void applyEndCap(double[] vertex, double[] other) {
    double offset = stroke.getLineWidth() / 2;
    switch (stroke.getEndCap()) {
    case BasicStroke.CAP_BUTT:
      // do nothing
      break;

    case BasicStroke.CAP_SQUARE:
      gl.glBegin(GL.GL_QUADS);
      if (vertex[0] == other[0]) {
        double direction = Math.signum(other[1] - vertex[1]);
        double x, y;
        x = vertex[0] + offset;
        y = vertex[1];
        gl.glVertex2d(x, y);
        y = vertex[1] - offset * direction;
        gl.glVertex2d(x, y);
        x = vertex[0] - offset;
        gl.glVertex2d(x, y);
        y = vertex[1];
        gl.glVertex2d(x, y);
      } else {
        double angle = Math.atan((vertex[1] - other[1]) / (vertex[0] - other[0]));
        double sin = Math.sin(angle) * offset;
        double cos = Math.cos(angle) * offset;
        double x, y;
        x = vertex[0] + sin;
        y = vertex[1] - cos;
        gl.glVertex2d(x, y);
        x = vertex[0] + sin - cos;
        y = vertex[1] + sin + cos;
        gl.glVertex2d(x, y);
        x = vertex[0] - sin - cos;
        y = vertex[1] + sin - cos;
        gl.glVertex2d(x, y);
        x = vertex[0] - sin;
        y = vertex[1] + cos;
        gl.glVertex2d(x, y);
      }

      gl.glEnd();
      break;

    case BasicStroke.CAP_ROUND:
      break;
    }
  }
}
