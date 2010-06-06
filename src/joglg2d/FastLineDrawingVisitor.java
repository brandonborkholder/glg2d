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

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

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
      drawBevelCorner(angle1, angle2);
      return;

    case BasicStroke.JOIN_ROUND:
      drawRoundCorner(angle1, angle2);
      return;

    case BasicStroke.JOIN_MITER:
      drawMiterCorner(angle1, angle2);
      return;

    default:
      throw new IllegalStateException("This class cannot support unknown line join");
    }
  }

  protected void drawRoundCorner(double angle1, double angle2) {
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

  protected void drawBevelCorner(double angle1, double angle2) {
    double sin1 = sin(angle1) * offset;
    double sin2 = sin(angle2) * offset;
    double cos1 = cos(angle1) * offset;
    double cos2 = cos(angle2) * offset;

    gl.glVertex2d(lastPoint[0] + sin1, lastPoint[1] - cos1);
    gl.glVertex2d(lastPoint[0] - sin1, lastPoint[1] + cos1);
    gl.glVertex2d(lastPoint[0] + sin2, lastPoint[1] - cos2);
    gl.glVertex2d(lastPoint[0] - sin2, lastPoint[1] + cos2);
  }

  protected void drawMiterCorner(double angle1, double angle2) {
    /*
     * If they are nearly parallel in the same direction, that could cause an
     * error in computing the intersections.
     */
    if (abs(angle1 - angle2) < 1e-10) {
      drawBevelCorner(angle1, angle2);
      return;
    }

    double sin1 = sin(angle1) * offset;
    double sin2 = sin(angle2) * offset;
    double cos1 = cos(angle1) * offset;
    double cos2 = cos(angle2) * offset;

    double[] intersect1 = intersection(sin1, cos1, sin2, cos2, true);
    double[] intersect2 = intersection(sin1, cos1, sin2, cos2, false);

    // If we exceed the miter limit, draw beveled corner
    double diffX = intersect1[0] - intersect2[0];
    double diffY = intersect1[1] - intersect2[1];
    double dist = sqrt(diffX * diffX + diffY * diffY);
    if (dist / stroke.getLineWidth() > stroke.getMiterLimit()) {
      drawBevelCorner(angle1, angle2);
    } else {
      gl.glVertex2d(lastPoint[0] + intersect1[0], lastPoint[1] + intersect1[1]);
      gl.glVertex2d(lastPoint[0] + intersect2[0], lastPoint[1] + intersect2[1]);
    }
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
   * Finds the intersection of two lines. This method was written to reduce the
   * number of array creations and so is quite dense. However, it is easy to
   * understand the theory behind the computation.
   *
   * <p>
   * We have two lines, the first with angle theta and the second with angle
   * phi. The angles are relative to the x-axis and computed by
   * {@link #angleOf(double[], double[])}. The arguments {@code sin1}, {@code
   * cos1}, {@code sin2}, {@code cos2} are the sin and cos of theta and phi
   * respectively, multiplied by half the line width. This gives us an easy way
   * to represent the line in parametric form. For example the first line (with
   * angle theta) has the form
   *
   * <pre>
   * &lt;x, y&gt; = &lt;sin1, -cos1&gt; + t * &lt;cos1, sin1&gt;
   * </pre>
   *
   * </p>
   * <p>
   * <code>&lt;sin1, -cos1&gt;</code> is a point on the line, while
   * <code>&lt;cos1, sin1&gt;</code> is the direction of the line. The method
   * for solving for the intersection of these two parametric lines is
   * straightforward. Let <code>o1</code> and <code>o2</code> be the points on
   * the lines and <code>v1</code> and <code>v2</code> be the two direction
   * vectors. Now we have
   *
   * <pre>
   * p1 = o1 + t * v1
   * p2 = o2 + s * v2
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
   * Solving for <code>t</code> is easy since we only have the z component. Put
   * <code>t</code> back into the first equation gives us our point of
   * intersection.
   * </p>
   * <p>
   * This is certainly not what is shown in the method body. But if you
   * construct the vectors <code>o1, o2, v1, v2</code> as described above, and
   * solve for t, you will get the body of the method. The extra parameter
   * {@code side} specifies which side to find the intersection. This only
   * affects the vectors <code>o1</code> and <code>o2</code>. If {@code side} is
   * {@code true}, then <code>o1 = &lt;sin1, -cos1&gt;</code>. If {@code side}
   * is {@code false}, then <code>o1 = &lt;-sin1, cos1&gt;</code>. Since the
   * miter join needs to calculate two intersections, one for each side of the
   * stroke, this enables both.
   * </p>
   */
  protected double[] intersection(double sin1, double cos1, double sin2, double cos2, boolean side) {
    if (side) {
      double t = sin2 * (sin2 - sin1) - cos2 * (cos1 - cos2);
      t /= sin2 * cos1 - cos2 * sin1;
      return new double[] { sin1 + t * cos1, -cos1 + t * sin1 };
    } else {
      double t = sin2 * (sin1 - sin2) - cos2 * (cos2 - cos1);
      t /= sin2 * cos1 - cos2 * sin1;
      return new double[] { -sin1 + t * cos1, cos1 + t * sin1 };
    }
  }

  protected static double angleOf(double[] vertex, double[] other) {
    return atan2(vertex[1] - other[1], vertex[0] - other[0]);
  }
}
