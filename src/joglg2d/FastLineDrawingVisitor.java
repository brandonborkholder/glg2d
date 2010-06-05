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
import java.util.Arrays;

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

  protected float offset;

  public FastLineDrawingVisitor(GL gl, BasicStroke stroke) {
    this.gl = gl;
    this.stroke = stroke;
    offset = stroke.getLineWidth() / 2;
  }

  @Override
  public void beginPoly(int windingRule) {
  }

  @Override
  public void closeLine() {
    // force drawing around the corner
    lineTo(firstPoint);
    lineTo(secondPoint);
    drawLineEnd();
    firstPoint = lastPoint = null;
    secondPoint = secondLastPoint = null;
    gl.glEnd();
  }

  @Override
  public void endPoly() {
    if (firstPoint != null) {
      drawLineEnd();
      gl.glEnd();
      applyEndCap(firstPoint, secondPoint);
      applyEndCap(lastPoint, secondLastPoint);
    } else {
      gl.glEnd();
    }
  }

  @Override
  public void lineTo(double[] vertex) {
    if (lastPoint[0] == vertex[0] && lastPoint[1] == vertex[1]) {
      return;
    }

    if (secondPoint == null) {
      secondPoint = vertex;
    }

    if (secondLastPoint == null) {
      drawLineStart();
    } else {
      drawCorner(vertex);
    }

    secondLastPoint = lastPoint;
    lastPoint = vertex;
  }

  protected void drawLineEnd() {
    double angle = angleOf(lastPoint, secondLastPoint);
    double cos = Math.cos(angle) * offset;
    double sin = Math.sin(angle) * offset;

    gl.glVertex2d(lastPoint[0] + sin, lastPoint[1] - cos);
    gl.glVertex2d(lastPoint[0] - sin, lastPoint[1] + cos);
  }

  protected void drawLineStart() {
    double angle = angleOf(secondPoint, firstPoint);
    double cos = Math.cos(angle) * offset;
    double sin = Math.sin(angle) * offset;

    gl.glVertex2d(firstPoint[0] + sin, firstPoint[1] - cos);
    gl.glVertex2d(firstPoint[0] - sin, firstPoint[1] + cos);
  }

  protected void drawCorner(double[] vertex) {
    double angle1 = angleOf(lastPoint, secondLastPoint);
    double angle2 = angleOf(vertex, lastPoint);

    double cos1 = Math.cos(angle1) * offset;
    double sin1 = Math.sin(angle1) * offset;
    double cos2 = Math.cos(angle2) * offset;
    double sin2 = Math.sin(angle2) * offset;

    if (Arrays.equals(lastPoint, secondLastPoint)) {
      gl.glVertex2d(lastPoint[0] + sin2, lastPoint[1] - cos2);
      gl.glVertex2d(lastPoint[0] - sin2, lastPoint[1] + cos2);
      return;
    } else if (Arrays.equals(lastPoint, vertex)) {
      gl.glVertex2d(lastPoint[0] + sin1, lastPoint[1] - cos1);
      gl.glVertex2d(lastPoint[0] - sin1, lastPoint[1] + cos1);
      return;
    }

    if (stroke.getLineJoin() == BasicStroke.JOIN_BEVEL) {
      gl.glVertex2d(lastPoint[0] + sin1, lastPoint[1] - cos1);
      gl.glVertex2d(lastPoint[0] - sin1, lastPoint[1] + cos1);
      gl.glVertex2d(lastPoint[0] + sin2, lastPoint[1] - cos2);
      gl.glVertex2d(lastPoint[0] - sin2, lastPoint[1] + cos2);
      return;
    } else if (stroke.getLineJoin() == BasicStroke.JOIN_ROUND) {
      gl.glVertex2d(lastPoint[0] + sin1, lastPoint[1] - cos1);
      gl.glVertex2d(lastPoint[0] - sin1, lastPoint[1] + cos1);
      if (angle2 < angle1) {
        angle2 += Math.PI * 2;
      }

      for (double angle = angle1; angle < angle2; angle += 0.5) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        gl.glVertex2d(lastPoint[0] + sin * offset, lastPoint[1] - cos * offset);
        gl.glVertex2d(lastPoint[0] - sin * offset, lastPoint[1] + cos * offset);
      }
      gl.glVertex2d(lastPoint[0] + sin2, lastPoint[1] - cos2);
      gl.glVertex2d(lastPoint[0] - sin2, lastPoint[1] + cos2);
      return;
    }
    // else BasicStroke.JOIN_MITER and continue

    double[] pt1 = new double[] { secondLastPoint[0] + sin1, secondLastPoint[1] - cos1 };
    double[] pt2 = new double[] { lastPoint[0] + sin2, lastPoint[1] - cos2 };
    double[] pt3 = new double[] { lastPoint[0] - secondLastPoint[0], lastPoint[1] - secondLastPoint[1] };
    double[] pt4 = new double[] { vertex[0] - lastPoint[0], vertex[1] - lastPoint[1] };
    double[] intersect = intersection(pt1, pt2, pt3, pt4);

    if (magnitude(pt3) > 0 && magnitude(pt4) > 0) {
      gl.glVertex2d(intersect[0], intersect[1]);
    } else {
      System.out.println(Arrays.toString(intersect));
    }

    pt1 = new double[] { secondLastPoint[0] - sin1, secondLastPoint[1] + cos1 };
    pt2 = new double[] { lastPoint[0] - sin2, lastPoint[1] + cos2 };
    pt3 = new double[] { lastPoint[0] - secondLastPoint[0], lastPoint[1] - secondLastPoint[1] };
    pt4 = new double[] { vertex[0] - lastPoint[0], vertex[1] - lastPoint[1] };
    intersect = intersection(pt1, pt2, pt3, pt4);
    if (magnitude(pt3) > 0 && magnitude(pt4) > 0) {
      gl.glVertex2d(intersect[0], intersect[1]);
    }
  }

  static double[] intersection(double[] origin1, double[] origin2, double[] vect1, double[] vect2) {
    if (Math.abs(vect1[0] / vect2[0] - vect1[1] / vect2[1]) < 1e-10 && magnitude(vect1) > 0 && magnitude(vect2) > 0) {
      System.out.println();
    }

    double[] crossV = crossProduct(vect1, vect2);
    double[] diffP = new double[] { origin2[0] - origin1[0], origin2[1] - origin1[1] };
    double[] crossPV = crossProduct(diffP, vect2);

    if (Math.abs(crossV[0] / crossPV[0] - crossV[1] / crossPV[1]) < 1e-10 && magnitude(crossV) > 0 && magnitude(crossPV) > 0) {
      System.out.println();
    }

    double a = magnitude(crossPV) / magnitude(crossV);

    double[] pt = new double[] { origin1[0] + a * vect1[0], origin1[1] + a * vect1[1] };
    return pt;
  }

  static double magnitude(double[] v) {
    double s = 0;
    for (double i : v) {
      s += i * i;
    }

    return Math.sqrt(s);
  }

  static double[] crossProduct(double[] a, double[] b) {
    // XXX assuming a, b are in the x,y plane
    return new double[] { 0, 0, a[0] * b[1] - a[1] * b[0] };
  }

  @Override
  public void moveTo(double[] vertex) {
    if (firstPoint != null) {
      drawLineEnd();
    }

    gl.glEnd();

    if (firstPoint != null) {
      applyEndCap(lastPoint, secondLastPoint);
      applyEndCap(firstPoint, secondPoint);
    }

    gl.glBegin(GL.GL_QUAD_STRIP);
    firstPoint = lastPoint = vertex;
    secondLastPoint = secondPoint = null;
  }

  protected void applyEndCap(double[] vertex, double[] other) {
    double offset = stroke.getLineWidth() / 2;
    switch (stroke.getEndCap()) {
    case BasicStroke.CAP_BUTT:
      // do nothing
      break;

    case BasicStroke.CAP_SQUARE:
      gl.glBegin(GL.GL_QUADS);
      double angle = angleOf(vertex, other);
      double sin = Math.sin(angle) * offset;
      double cos = Math.cos(angle) * offset;
      double x = vertex[0] + sin;
      double y = vertex[1] - cos;
      gl.glVertex2d(x, y);
      x = vertex[0] - sin;
      y = vertex[1] + cos;
      gl.glVertex2d(x, y);
      x = vertex[0] - sin + cos;
      y = vertex[1] + sin + cos;
      gl.glVertex2d(x, y);
      x = vertex[0] + sin + cos;
      y = vertex[1] + sin - cos;
      gl.glVertex2d(x, y);

      gl.glEnd();
      break;

    case BasicStroke.CAP_ROUND:
      gl.glBegin(GL.GL_TRIANGLE_FAN);
      angle = angleOf(vertex, other);
      gl.glVertex2d(vertex[0] + Math.cos(angle) * .1, vertex[1] + Math.sin(angle) * .1);
      double step = 0.5;
      double maxAngle = angle + Math.PI + step;
      for (double theta = angle; theta < maxAngle; theta += step) {
        x = vertex[0] + Math.sin(theta) * offset;
        y = vertex[1] - Math.cos(theta) * offset;
        gl.glVertex2d(x, y);
      }
      gl.glEnd();
      break;
    }
  }

  protected static double angleOf(double[] vertex, double[] other) {
    return Math.atan2(vertex[1] - other[1], vertex[0] - other[0]);
  }
}
