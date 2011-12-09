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

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.awt.BasicStroke;

import javax.media.opengl.GL;

/**
 * Draws a line, as outlined by a {@link BasicStroke}. The current
 * implementation supports everything except dashes. This class draws a series
 * of quads for each line segment, joins corners and endpoints as appropriate.
 */
public class LineDrawingVisitor extends SimplePathVisitor {
  protected GL gl;

  protected int lineJoin;

  protected int endCap;

  protected float lineOffset;

  protected float miterLimit;

  protected float[] lastPoint;

  protected float[] secondLastPoint;

  protected float[] firstPoint;

  protected float[] secondPoint;

  protected VertexBuffer vBuffer = VertexBuffer.getSharedBuffer();

  @Override
  public void setGLContext(GL context) {
    gl = context;
  }

  @Override
  public void setStroke(BasicStroke stroke) {
    lineJoin = stroke.getLineJoin();
    lineOffset = stroke.getLineWidth() / 2;
    endCap = stroke.getEndCap();
    miterLimit = stroke.getMiterLimit();
  }

  @Override
  public void beginPoly(int windingRule) {
    vBuffer.clear();
    firstPoint = secondPoint = null;
    lastPoint = secondLastPoint = null;

    /*
     * pen hangs down and to the right. See java.awt.Graphics
     */
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glPushMatrix();
    gl.glTranslatef(0.5f, 0.5f, 0);
  }

  @Override
  public void closeLine() {
    // force drawing around the corner
    lineTo(firstPoint);
    if (secondPoint != null) {
      lineTo(secondPoint);
      endLine();
      drawBuffer(GL.GL_QUAD_STRIP);
    }

    firstPoint = lastPoint = null;
    secondPoint = secondLastPoint = null;
  }

  @Override
  public void endPoly() {
    if (firstPoint != null && secondPoint != null) {
      endLine();
      drawBuffer(GL.GL_QUAD_STRIP);

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
      drawBuffer(GL.GL_QUAD_STRIP);

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

  protected float[] lineCorners(float[] linePoint1, float[] linePoint2, float[] vertex, float offset) {
    float[] translated = new float[2];
    translated[0] = linePoint2[0] - linePoint1[0];
    translated[1] = linePoint2[1] - linePoint1[1];

    float norm = translated[0] * translated[0] + translated[1] * translated[1];
    norm = (float) sqrt(norm);

    float scale = offset / norm;
    float[] corners = new float[4];
    corners[0] = translated[1] * scale + vertex[0];
    corners[1] = -translated[0] * scale + vertex[1];
    corners[2] = -translated[1] * scale + vertex[0];
    corners[3] = translated[0] * scale + vertex[1];
    return corners;
  }

  /**
   * Finds the intersection of two lines. This method was written to reduce the
   * number of array creations and so is quite dense. However, it is easy to
   * understand the theory behind the computation. I found this at <a
   * href="http://mathforum.org/library/drmath/view/62814.html"
   * >http://mathforum.org/library/drmath/view/62814.html</a>.
   *
   * <p>
   * We have two lines, specified by three points (P1, P2, P3). They share the
   * second point. This gives us an easy way to represent the line in parametric
   * form. For example the first line has the form
   *
   * <pre>
   * &lt;x, y&gt; = &lt;P1<sub>x</sub>, P1<sub>y</sub>&gt; + t * &lt;P2<sub>x</sub>-P1<sub>x</sub>, P2<sub>y</sub>-P1<sub>y</sub>&gt;
   * </pre>
   *
   * </p>
   * <p>
   * <code>&lt;P1<sub>x</sub>, P1<sub>y</sub>&gt;</code> is a point on the line,
   * while
   * <code>&lt;P2<sub>x</sub>-P1<sub>x</sub>, P2<sub>y</sub>-P1<sub>y</sub>&gt;</code>
   * is the direction of the line. The method for solving for the intersection
   * of these two parametric lines is straightforward. Let <code>o1</code> and
   * <code>o2</code> be the points on the lines and <code>v1</code> and
   * <code>v2</code> be the two direction vectors. Now we have
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
   * This method solves for <code>t</code>, but not directly for lines
   * intersecting the point parameters. Since we're trying to use this for the
   * miter corners, we want to solve for the intersections of the two outside
   * edges of the lines that go from <code>secondLastPoint</code> to
   * <code>lastPoint</code> and from <code>lastPoint</code> to
   * <code>point</code>.
   * </p>
   */
  protected float[] getMiterIntersections(float[] secondLastPoint, float[] lastPoint, float[] point) {
    float[] o1 = lineCorners(secondLastPoint, lastPoint, lastPoint, lineOffset);
    float[] o2 = lineCorners(lastPoint, point, lastPoint, lineOffset);

    float[] v1 = new float[2];
    v1[0] = lastPoint[0] - secondLastPoint[0];
    v1[1] = lastPoint[1] - secondLastPoint[1];
    float[] v2 = new float[2];
    v2[0] = lastPoint[0] - point[0];
    v2[1] = lastPoint[1] - point[1];

    float norm = (float) sqrt(v1[0] * v1[0] + v1[1] * v1[1]);
    v1[0] /= norm;
    v1[1] /= norm;
    norm = (float) sqrt(v2[0] * v2[0] + v2[1] * v2[1]);
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

    float norm = (float) sqrt(v_x * v_x + v_y * v_y);
    v_x *= lineOffset / norm;
    v_y *= lineOffset / norm;

    float[] corners = lineCorners(lastPoint, point, point, lineOffset);

    addVertex(corners[0], corners[1]);
    addVertex(corners[0] + v_x, corners[1] + v_y);
    addVertex(corners[2] + v_x, corners[3] + v_y);
    addVertex(corners[2], corners[3]);
    drawBuffer(GL.GL_QUADS);
  }

  protected void drawCapRound(float[] lastPoint, float[] point) {
    float step = 0.5f;
    float cos = (float) cos(step);
    float sin = (float) sin(step);

    // put the corner around 0,0
    float[] corner = lineCorners(lastPoint, point, new float[2], lineOffset);

    addVertex(point[0], point[1]);
    addVertex(point[0] + corner[0], point[1] + corner[1]);

    float max = (float) Math.PI / step + 1;
    for (int i = 0; i < max; i++) {
      // rotate it
      float x = cos * corner[0] - sin * corner[1];
      float y = sin * corner[0] + cos * corner[1];
      corner[0] = x;
      corner[1] = y;

      // translate back
      addVertex(point[0] + corner[0], point[1] + corner[1]);
    }
    drawBuffer(GL.GL_TRIANGLE_FAN);
  }

  protected void addVertex(float x, float y) {
    vBuffer.addVertex(x, y);
  }

  protected void drawBuffer(int mode) {
    vBuffer.drawBuffer(gl, mode);
  }
}
