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

  @Override
  public void lineTo(double[] vertex) {
    // ignore 0-length lines
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
    double cos = cos(angle) * offset;
    double sin = sin(angle) * offset;

    gl.glVertex2d(lastPoint[0] + sin, lastPoint[1] - cos);
    gl.glVertex2d(lastPoint[0] - sin, lastPoint[1] + cos);
  }

  protected void drawLineStart() {
    double angle = angleOf(secondPoint, firstPoint);
    double cos = cos(angle) * offset;
    double sin = sin(angle) * offset;

    gl.glVertex2d(firstPoint[0] + sin, firstPoint[1] - cos);
    gl.glVertex2d(firstPoint[0] - sin, firstPoint[1] + cos);
  }

  protected void drawCorner(double[] vertex) {
    double angle1 = angleOf(lastPoint, secondLastPoint);
    double angle2 = angleOf(vertex, lastPoint);

    switch (stroke.getLineJoin()) {
    case BasicStroke.JOIN_BEVEL:
      drawBevelCorner(angle1, angle2, vertex);
      return;

    case BasicStroke.JOIN_ROUND:
      drawRoundCorner(angle1, angle2, vertex);
      return;

    case BasicStroke.JOIN_MITER:
      drawMiterCorner(angle1, angle2, vertex);
      return;

    default:
      throw new IllegalStateException("This class cannot support unknown line join");
    }
  }

  protected void drawRoundCorner(double angle1, double angle2, double[] vertex) {
    double sin1 = sin(angle1) * offset;
    double sin2 = sin(angle2) * offset;
    double cos1 = cos(angle1) * offset;
    double cos2 = cos(angle2) * offset;

    gl.glVertex2d(lastPoint[0] + sin1, lastPoint[1] - cos1);
    gl.glVertex2d(lastPoint[0] - sin1, lastPoint[1] + cos1);
    if (angle2 < angle1) {
      angle2 += Math.PI * 2;
    }

    for (double angle = angle1; angle < angle2; angle += 0.5) {
      double sin = sin(angle);
      double cos = cos(angle);
      gl.glVertex2d(lastPoint[0] + sin * offset, lastPoint[1] - cos * offset);
      gl.glVertex2d(lastPoint[0] - sin * offset, lastPoint[1] + cos * offset);
    }
    gl.glVertex2d(lastPoint[0] + sin2, lastPoint[1] - cos2);
    gl.glVertex2d(lastPoint[0] - sin2, lastPoint[1] + cos2);
  }

  protected void drawBevelCorner(double angle1, double angle2, double[] vertex) {
    double sin1 = sin(angle1) * offset;
    double sin2 = sin(angle2) * offset;
    double cos1 = cos(angle1) * offset;
    double cos2 = cos(angle2) * offset;

    gl.glVertex2d(lastPoint[0] + sin1, lastPoint[1] - cos1);
    gl.glVertex2d(lastPoint[0] - sin1, lastPoint[1] + cos1);
    gl.glVertex2d(lastPoint[0] + sin2, lastPoint[1] - cos2);
    gl.glVertex2d(lastPoint[0] - sin2, lastPoint[1] + cos2);
  }

  protected void drawMiterCorner(double angle1, double angle2, double[] vertex) {
    if (exceedsMiterLimit(angle1, angle2)) {
      drawBevelCorner(angle1, angle2, vertex);
      return;
    }

    double sin1 = sin(angle1) * offset;
    double sin2 = sin(angle2) * offset;
    double cos1 = cos(angle1) * offset;
    double cos2 = cos(angle2) * offset;

    double[] pt1 = new double[] { secondLastPoint[0] + sin1, secondLastPoint[1] - cos1 };
    double[] pt2 = new double[] { lastPoint[0] + sin2, lastPoint[1] - cos2 };
    double[] pt3 = new double[] { lastPoint[0] - secondLastPoint[0], lastPoint[1] - secondLastPoint[1] };
    double[] pt4 = new double[] { vertex[0] - lastPoint[0], vertex[1] - lastPoint[1] };
    double[] intersect = intersection(pt1, pt2, pt3, pt4);
    gl.glVertex2d(intersect[0], intersect[1]);

    pt1 = new double[] { secondLastPoint[0] - sin1, secondLastPoint[1] + cos1 };
    pt2 = new double[] { lastPoint[0] - sin2, lastPoint[1] + cos2 };
    pt3 = new double[] { lastPoint[0] - secondLastPoint[0], lastPoint[1] - secondLastPoint[1] };
    pt4 = new double[] { vertex[0] - lastPoint[0], vertex[1] - lastPoint[1] };
    intersect = intersection(pt1, pt2, pt3, pt4);
    gl.glVertex2d(intersect[0], intersect[1]);
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
      double sin = sin(angle) * offset;
      double cos = cos(angle) * offset;
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
      gl.glVertex2d(vertex[0] + cos(angle) * .1, vertex[1] + sin(angle) * .1);
      double step = 0.5;
      double maxAngle = angle + Math.PI + step;
      for (double theta = angle; theta < maxAngle; theta += step) {
        x = vertex[0] + sin(theta) * offset;
        y = vertex[1] - cos(theta) * offset;
        gl.glVertex2d(x, y);
      }
      gl.glEnd();
      break;
    }
  }

  /**
   * Returns true if the miter join is not appropriate for these angles. This
   * not only takes into account the miter limit of BasicStroke, but also
   * determines if <code>angle1</code> and <code>angle2</code> are such that the
   * derivation of the miter points would encounter an error.
   */
  protected boolean exceedsMiterLimit(double angle1, double angle2) {
    // TODO
    return false;
  }

  /**
   * Finds the intersection of two lines in parametric form. Letting
   * <code>o1=origin1</code> and <code>v1=vect1</code> (similarly for
   * <code>origin2, vect2</code>) we have that
   *
   * <pre>
   * P1 = o1 + t * v1
   * P2 = o2 + s * v2
   * </pre>
   *
   * We can solve to find the intersection by
   *
   * <pre>
   * o1 + t * v1 = o2 + s * v2
   * t * v1 = o2 - o1 + s * v2
   * (t * v1) x v2 = (o2 - o1 + s * v2) x v2    ; cross product by v2
   * t * (v1 x v2) = (o2 - o1) x v2             ; to get rid of s term
   * </pre>
   *
   * Solve for <code>t</code> by finding the ratio of the magnitudes. Now just
   * put <code>t</code> back into the first equation gives us our point of
   * intersection. Note that I didn't come up with this, but it's pretty easy to
   * determine.
   */
  static double[] intersection(double[] origin1, double[] origin2, double[] vect1, double[] vect2) {
    // v1 x v2
    double crossVz = crossProductZ(vect1, vect2);
    // o2 - o1
    double[] diffP = new double[] { origin2[0] - origin1[0], origin2[1] - origin1[1] };
    // (o2 - o1) x v2
    double crossPVz = crossProductZ(diffP, vect2);

    double t = crossPVz / crossVz;
    double[] pt = new double[] { origin1[0] + t * vect1[0], origin1[1] + t * vect1[1] };
    return pt;
  }

  /**
   * Since all our drawing is in the x,y plane, the only non-zero component of
   * the cross-product is z.
   */
  protected static double crossProductZ(double[] a, double[] b) {
    return a[0] * b[1] - a[1] * b[0];
  }

  /*
   * XXX I don't know how slow cos is, but given how much I use it, this might
   * be optimized to the first three terms of the Taylor series.
   */
  protected static double cos(double angle) {
    return Math.cos(angle);
  }

  protected static double sin(double angle) {
    return Math.sin(angle);
  }

  protected static double angleOf(double[] vertex, double[] other) {
    return Math.atan2(vertex[1] - other[1], vertex[0] - other[0]);
  }
}
