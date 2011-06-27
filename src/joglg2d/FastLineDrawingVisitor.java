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

package joglg2d;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.awt.BasicStroke;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

/**
 * @author borkholder
 * @created May 11, 2010
 */
public class FastLineDrawingVisitor extends SimplePathVisitor {
  protected final GL gl;

  protected int lineJoin;

  protected int endCap;

  protected float lineOffset;

  protected float miterLimit;

  protected float[] lastPoint;

  protected float[] secondLastPoint;

  protected float[] firstPoint;

  protected float[] secondPoint;

  protected FloatBuffer buffer = BufferUtil.newFloatBuffer(300);

  public FastLineDrawingVisitor(GL gl) {
    this.gl = gl;
  }

  public void setStroke(BasicStroke stroke) {
    lineJoin = stroke.getLineJoin();
    lineOffset = stroke.getLineWidth() / 2;
    endCap = stroke.getEndCap();
    miterLimit = stroke.getMiterLimit();
  }

  @Override
  public void beginPoly(int windingRule) {
    firstPoint = secondPoint = null;
    lastPoint = secondLastPoint = null;

    /*
     * pen hangs down and to the right. See java.awt.Graphics
     */
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glPushMatrix();
    gl.glTranslatef(1, 1, 0);

    // use vertex arrays
    gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
  }

  @Override
  public void closeLine() {
    // force drawing around the corner
    lineTo(firstPoint);
    if (secondPoint != null) {
      lineTo(secondPoint);
      endLine();
      drawBuffer();
    }

    firstPoint = lastPoint = null;
    secondPoint = secondLastPoint = null;
  }

  @Override
  public void endPoly() {
    if (firstPoint != null && secondPoint != null) {
      endLine();
      drawBuffer();

      applyEndCap(secondPoint, firstPoint);
      applyEndCap(secondLastPoint, lastPoint);
    }

    gl.glPopMatrix();
    gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
  }

  @Override
  public void moveTo(float[] vertex) {
    if (firstPoint != null) {
      endLine();
      drawBuffer();

      applyEndCap(secondLastPoint, lastPoint);
      applyEndCap(secondPoint, firstPoint);
    }

    lastPoint = new float[] { vertex[0], vertex[1] };
    firstPoint = lastPoint;
    secondLastPoint = secondPoint = null;
  }

  @Override
  public void lineTo(float[] vertex) {
    // ignore 0-length lines
    if (lastPoint[0] == vertex[0] && lastPoint[1] == vertex[1]) {
      return;
    }

    float[] vClone = new float[] { vertex[0], vertex[1] };
    if (secondPoint == null) {
      secondPoint = vClone;
    }

    if (secondLastPoint == null) {
      startLine();
    } else {
      drawCorner(vertex);
    }

    secondLastPoint = lastPoint;
    lastPoint = vClone;
  }

  @Override
  public void quadTo(float[] previousVertex, float[] control) {
    int originalJoin = lineJoin;
    lineJoin = BasicStroke.JOIN_BEVEL;
    super.quadTo(previousVertex, control);
    lineJoin = originalJoin;
  }

  @Override
  public void cubicTo(float[] previousVertex, float[] control) {
    int originalJoin = lineJoin;
    lineJoin = BasicStroke.JOIN_BEVEL;
    super.cubicTo(previousVertex, control);
    lineJoin = originalJoin;
  }

  protected void endLine() {
    float[] corners = lineCorners(secondLastPoint, lastPoint, lastPoint, lineOffset);
    addVertex(corners[0], corners[1]);
    addVertex(corners[2], corners[3]);
  }

  protected void startLine() {
    float[] corners = lineCorners(firstPoint, secondPoint, firstPoint, lineOffset);
    addVertex(corners[0], corners[1]);
    addVertex(corners[2], corners[3]);
  }

  protected void drawCorner(float[] vertex) {
    switch (lineJoin) {
    case BasicStroke.JOIN_BEVEL:
      drawCornerBevel(secondLastPoint, lastPoint, vertex);
      return;

    case BasicStroke.JOIN_ROUND:
      drawCornerRound(secondLastPoint, lastPoint, vertex);
      return;

    case BasicStroke.JOIN_MITER:
      drawCornerMiter(secondLastPoint, lastPoint, vertex);
      return;

    default:
      throw new IllegalStateException("This class cannot support unknown line join");
    }
  }

  protected void drawRoundCorner(double angle1, double angle2) {
    float sin1 = (float) sin(angle1) * lineOffset;
    float sin2 = (float) sin(angle2) * lineOffset;
    float cos1 = (float) cos(angle1) * lineOffset;
    float cos2 = (float) cos(angle2) * lineOffset;

    addVertex(lastPoint[0] + sin1, lastPoint[1] - cos1);
    addVertex(lastPoint[0] - sin1, lastPoint[1] + cos1);
    if (angle2 < angle1) {
      angle2 += Math.PI * 2;
    }

    for (double angle = angle1; angle < angle2; angle += 0.5) {
      float sin = (float) sin(angle);
      float cos = (float) cos(angle);
      addVertex(lastPoint[0] + sin * lineOffset, lastPoint[1] - cos * lineOffset);
      addVertex(lastPoint[0] - sin * lineOffset, lastPoint[1] + cos * lineOffset);
    }
    addVertex(lastPoint[0] + sin2, lastPoint[1] - cos2);
    addVertex(lastPoint[0] - sin2, lastPoint[1] + cos2);
  }

  protected void drawCornerRound(float[] secondLastPoint, float[] lastPoint, float[] point) {
    float step = 0.5f;
    float cos = (float) cos(step);
    float sin = (float) sin(step);

    // translate to 0,0
    float[] corner = lineCorners(secondLastPoint, lastPoint, new float[2], lineOffset);

    addVertex(lastPoint[0] + corner[0], lastPoint[1] + corner[1]);
    addVertex(lastPoint[0] - corner[0], lastPoint[1] - corner[1]);

    float max = 2 * (float) Math.PI / step + 1;
    for (int i = 0; i < max; i++) {
      // rotate it
      float x = cos * corner[0] - sin * corner[1];
      float y = sin * corner[0] + cos * corner[1];
      corner[0] = x;
      corner[1] = y;

      // translate back
      addVertex(lastPoint[0] + corner[0], lastPoint[1] + corner[1]);
      addVertex(lastPoint[0] - corner[0], lastPoint[1] - corner[1]);
    }

    corner = lineCorners(lastPoint, point, lastPoint, lineOffset);
    addVertex(corner[0], corner[1]);
    addVertex(corner[2], corner[3]);
  }

  protected void drawCornerBevel(float[] secondLastPoint, float[] lastPoint, float[] point) {
    float[] corners = lineCorners(secondLastPoint, lastPoint, lastPoint, lineOffset);
    addVertex(corners[0], corners[1]);
    addVertex(corners[2], corners[3]);

    corners = lineCorners(lastPoint, point, lastPoint, lineOffset);
    addVertex(corners[0], corners[1]);
    addVertex(corners[2], corners[3]);
  }

  protected void drawBevelCorner(double angle1, double angle2) {
    float sin1 = (float) sin(angle1) * lineOffset;
    float sin2 = (float) sin(angle2) * lineOffset;
    float cos1 = (float) cos(angle1) * lineOffset;
    float cos2 = (float) cos(angle2) * lineOffset;

    addVertex(lastPoint[0] + sin1, lastPoint[1] - cos1);
    addVertex(lastPoint[0] - sin1, lastPoint[1] + cos1);
    addVertex(lastPoint[0] + sin2, lastPoint[1] - cos2);
    addVertex(lastPoint[0] - sin2, lastPoint[1] + cos2);
  }

  protected float[] lineCorners(float[] linePoint1, float[] linePoint2, float[] vertex, float offset) {
    float[] translated = new float[2];
    translated[0] = linePoint2[0] - linePoint1[0];
    translated[1] = linePoint2[1] - linePoint1[1];

    float norm = translated[0] * translated[0] + translated[1] * translated[1];
    norm = (float) Math.sqrt(norm);

    float scale = offset / norm;
    float[] corners = new float[4];
    corners[0] = translated[1] * scale + vertex[0];
    corners[1] = -translated[0] * scale + vertex[1];
    corners[2] = -translated[1] * scale + vertex[0];
    corners[3] = translated[0] * scale + vertex[1];
    return corners;
  }

  protected void drawCornerMiter(float[] secondLastPoint, float[] lastPoint, float[] point) {
    float[] intersection = getMiterIntersections(secondLastPoint, lastPoint, point);

    // If we exceed the miter limit, draw beveled corner
    double diffX = intersection[0] - intersection[2];
    double diffY = intersection[1] - intersection[3];
    double distSq = diffX * diffX + diffY * diffY;

    float lineWidth = lineOffset * 2;
    if (distSq / (lineWidth * lineWidth) > miterLimit * miterLimit) {
      drawCornerBevel(secondLastPoint, lastPoint, point);
    } else {
      addVertex(intersection[0], intersection[1]);
      addVertex(intersection[2], intersection[3]);
    }
  }

  protected float[] getMiterIntersections(float[] secondLastPoint, float[] lastPoint, float[] point) {
    float[] o1 = lineCorners(secondLastPoint, lastPoint, lastPoint, lineOffset);
    float[] o2 = lineCorners(lastPoint, point, lastPoint, lineOffset);

    float[] v1 = new float[2];
    v1[0] = lastPoint[0] - secondLastPoint[0];
    v1[1] = lastPoint[1] - secondLastPoint[1];
    float[] v2 = new float[2];
    v2[0] = lastPoint[0] - point[0];
    v2[1] = lastPoint[1] - point[1];

    float norm = (float) Math.sqrt(v1[0] * v1[0] + v1[1] * v1[1]);
    v1[0] /= norm;
    v1[1] /= norm;
    norm = (float) Math.sqrt(v2[0] * v2[0] + v2[1] * v2[1]);
    v2[0] /= norm;
    v2[1] /= norm;

    float[] intersections = new float[4];

    float t = (o2[0] - o1[0]) * v2[1] - (o2[1] - o1[1]) * v2[0];
    t /= v1[0] * v2[1] - v1[1] * v2[0];
    intersections[0] = o1[0] + t * v1[0];
    intersections[1] = o1[1] + t * v1[1];

    t = (o2[2] - o1[2]) * v2[1] - (o2[3] - o1[3]) * v2[0];
    t /= v1[0] * v2[1] - v1[1] * v2[0];
    intersections[2] = o1[2] + t * v1[0];
    intersections[3] = o1[3] + t * v1[1];

    return intersections;
  }

  /**
   * <a href=
   * "http://www.softsurfer.com/Archive/algorithm_0104/algorithm_0104B.htm#Line%20Intersections"
   * >http://www.softsurfer.com/Archive/algorithm_0104/algorithm_0104B.htm#Line%
   * 20Intersections</a>
   */
  protected float[] intersection2(float[] secondLastPoint, float[] lastPoint, float[] point) {
    float[] v1 = lineCorners(secondLastPoint, lastPoint, lastPoint, lineOffset);
    float[] v2 = lineCorners(lastPoint, point, lastPoint, lineOffset);

    float[] u = new float[2];
    u[0] = lastPoint[0] - secondLastPoint[0];
    u[1] = lastPoint[1] - secondLastPoint[1];
    float[] v = new float[2];
    v[0] = lastPoint[0] - point[0];
    v[1] = lastPoint[1] - point[1];

    float norm = (float) Math.sqrt(u[0] * u[0] + u[1] * u[1]);
    u[0] /= norm;
    u[1] /= norm;
    norm = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1]);
    v[0] /= norm;
    v[1] /= norm;

    float[] w = new float[] { v1[0] - v2[0], v1[1] - v2[1] };

    float t = (v1[1] - v2[1]) * u[0] - (v1[0] - v2[0]) * u[1];
    t /= u[0] * v[1] - u[1] * v[0];

    float[] intersections = new float[4];
    intersections[0] = v2[0] + t * v[0];
    intersections[1] = v2[1] + t * v[1];

    w = new float[] { v1[2] - v2[2], v1[3] - v2[3] };
    t = u[0] * w[1] - u[1] * w[0];
    t /= u[0] * v[1] - u[1] * v[0];

    intersections[2] = v2[2] + t * v[0];
    intersections[3] = v2[3] + t * v[1];

    return intersections;
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

    float sin1 = (float) sin(angle1) * lineOffset;
    float sin2 = (float) sin(angle2) * lineOffset;
    float cos1 = (float) cos(angle1) * lineOffset;
    float cos2 = (float) cos(angle2) * lineOffset;

    float[] intersect1 = intersection(sin1, cos1, sin2, cos2, true);
    float[] intersect2 = intersection(sin1, cos1, sin2, cos2, false);

    // If we exceed the miter limit, draw beveled corner
    double diffX = intersect1[0] - intersect2[0];
    double diffY = intersect1[1] - intersect2[1];
    double dist = sqrt(diffX * diffX + diffY * diffY);

    float lineWidth = lineOffset * 2;
    if (dist / lineWidth > miterLimit) {
      drawBevelCorner(angle1, angle2);
    } else {
      addVertex(lastPoint[0] + intersect1[0], lastPoint[1] + intersect1[1]);
      addVertex(lastPoint[0] + intersect2[0], lastPoint[1] + intersect2[1]);
    }
  }

  protected void applyEndCap(float[] lastPoint, float[] vertex) {
    switch (endCap) {
    case BasicStroke.CAP_BUTT:
      drawCapButt(lastPoint, vertex);
      break;

    case BasicStroke.CAP_SQUARE:
      drawCapSquare(lastPoint, vertex);
      break;

    case BasicStroke.CAP_ROUND:
      drawCapRound(lastPoint, vertex);
      break;
    }
  }

  protected void drawCapButt(float[] lastPoint, float[] point) {
    // do nothing
  }

  protected void drawCapSquare(float[] lastPoint, float[] point) {
    float v_x = point[0] - lastPoint[0];
    float v_y = point[1] - lastPoint[1];

    float norm = (float) Math.sqrt(v_x * v_x + v_y * v_y);
    v_x *= lineOffset / norm;
    v_y *= lineOffset / norm;

    float[] corners = lineCorners(lastPoint, point, point, lineOffset);

    gl.glBegin(GL.GL_QUADS);
    gl.glVertex2f(corners[0], corners[1]);
    gl.glVertex2f(corners[0] + v_x, corners[1] + v_y);
    gl.glVertex2f(corners[2] + v_x, corners[3] + v_y);
    gl.glVertex2f(corners[2], corners[3]);
    gl.glEnd();
  }

  protected void drawCapRound(float[] lastPoint, float[] point) {
    float step = 0.5f;
    float cos = (float) cos(step);
    float sin = (float) sin(step);

    // put the corner around 0,0
    float[] corner = lineCorners(lastPoint, point, new float[2], lineOffset);

    gl.glBegin(GL.GL_TRIANGLE_FAN);
    gl.glVertex2f(point[0], point[1]);
    gl.glVertex2f(point[0] + corner[0], point[1] + corner[1]);

    float max = (float) Math.PI / step + 1;
    for (int i = 0; i < max; i++) {
      // rotate it
      float x = cos * corner[0] - sin * corner[1];
      float y = sin * corner[0] + cos * corner[1];
      corner[0] = x;
      corner[1] = y;

      // translate back
      gl.glVertex2f(point[0] + corner[0], point[1] + corner[1]);
    }
    gl.glEnd();
  }

  /**
   * Finds the intersection of two lines. This method was written to reduce the
   * number of array creations and so is quite dense. However, it is easy to
   * understand the theory behind the computation.
   * 
   * <p>
   * We have two lines, the first with angle theta and the second with angle
   * phi. The angles are relative to the x-axis and computed by
   * {@link #angleOf(double[], double[])}. The arguments {@code sin1},
   * {@code cos1}, {@code sin2}, {@code cos2} are the sin and cos of theta and
   * phi respectively, multiplied by half the line width. This gives us an easy
   * way to represent the line in parametric form. For example the first line
   * (with angle theta) has the form
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
  protected float[] intersection(float sin1, float cos1, float sin2, float cos2, boolean side) {
    if (side) {
      float t = sin2 * (sin2 - sin1) - cos2 * (cos1 - cos2);
      t /= sin2 * cos1 - cos2 * sin1;
      return new float[] { sin1 + t * cos1, -cos1 + t * sin1 };
    } else {
      float t = sin2 * (sin1 - sin2) - cos2 * (cos2 - cos1);
      t /= sin2 * cos1 - cos2 * sin1;
      return new float[] { -sin1 + t * cos1, cos1 + t * sin1 };
    }
  }

  protected static double angleOf(float[] vertex, float[] other) {
    return atan2(vertex[1] - other[1], vertex[0] - other[0]);
  }

  protected void addVertex(float x, float y) {
    if (buffer.position() == buffer.capacity()) {
      FloatBuffer larger = BufferUtil.newFloatBuffer(buffer.position() * 2);
      buffer.rewind();
      larger.put(buffer);
      buffer = larger;
    }

    buffer.put(x);
    buffer.put(y);
  }

  protected void drawBuffer() {
    if (buffer.position() == 0) {
      return;
    }

    int size = buffer.position() / 2;
    buffer.rewind();
    gl.glVertexPointer(2, GL.GL_FLOAT, 0, buffer);
    gl.glDrawArrays(GL.GL_QUAD_STRIP, 0, size);
  }
}
