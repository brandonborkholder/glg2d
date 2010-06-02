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
import java.awt.Color;
import java.nio.DoubleBuffer;
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

  protected DoubleBuffer buffer = DoubleBuffer.allocate(500);

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
    connect2(secondPoint);
    firstPoint = lastPoint = null;
    secondPoint = secondLastPoint = null;
    gl.glEnd();
  }

  @Override
  public void endPoly() {
    gl.glEnd();

    if (firstPoint != null) {
      applyEndCap(firstPoint, secondPoint);
      applyEndCap(lastPoint, secondLastPoint);
    }

    drawConnections();
  }

  @Override
  public void lineTo(double[] vertex) {
    double angle = angleOf(vertex, lastPoint);
    double offset = stroke.getLineWidth() / 2;
    double sin = Math.sin(angle) * offset;
    double cos = Math.cos(angle) * offset;
    double x = lastPoint[0] + sin;
    double y = lastPoint[1] - cos;
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

    connect2(vertex);
    secondLastPoint = lastPoint;
    lastPoint = vertex;
    if (secondPoint == null) {
      secondPoint = vertex;
    }
  }

  protected void drawConnections() {
    if (buffer.position() == 0) {
      return;
    }

    Color c = Color.red;
    gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL.GL_CURRENT_BIT);
    gl.glColor3ub((byte) c.getRed(), (byte) c.getGreen(), (byte) c.getBlue());
    gl.glPointSize(4);
    // gl.glBegin(GL.GL_QUADS);
    gl.glBegin(GL.GL_POINTS);

    buffer.limit(buffer.position());
    buffer.rewind();
    while (buffer.hasRemaining()) {
      gl.glVertex2d(buffer.get(), buffer.get());
    }
    buffer.rewind();
    buffer.limit(buffer.capacity());

    gl.glEnd();
    gl.glPopAttrib();
  }

  protected void connect2(double[] vertex) {
    // connect1(vertex);
    // connect3(vertex);
    // connect4(vertex);
    connect5(vertex);
  }

  protected void connect4(double[] vertex) {
    if (secondLastPoint == null || stroke.getLineJoin() != BasicStroke.JOIN_MITER) {
      return;
    }

    double angle1 = angleOf(lastPoint, secondLastPoint);
    double angle2 = angleOf(vertex, lastPoint);

    if (Math.abs(angle1 - Math.PI / 2) < 1e-10) {
      return;
    } else if (Math.abs(angle2 - Math.PI / 2) < 1e-10) {
      return;
    }

    // double x1 = secondLastPoint[0];
    // double y1 = secondLastPoint[1];
    double x2 = lastPoint[0];
    double y2 = lastPoint[1];
    // double x3 = vertex[0];
    // double y3 = vertex[1];
    double offset = stroke.getLineWidth() / 2;
    double a1 = y2 - x2 * Math.tan(angle1);
    double a2 = y2 - x2 * Math.tan(angle2);

    double b1 = offset / Math.cos(angle1);
    double b2 = offset / Math.cos(angle2);

    double xInt1 = (b2 - b1 + a2 - a1) / (Math.tan(angle1) - Math.tan(angle2));
    double yInt1 = xInt1 * Math.tan(angle1) + a1 + b1;
    double xInt2 = (-b2 + b1 + a2 - a1) / (Math.tan(angle1) - Math.tan(angle2));
    double yInt2 = xInt2 * Math.tan(angle1) + a1 - b1;

    if (yInt1 < 0) {
      System.out.println(String.format("%.2f, %.2f : %.2f, %.2f", xInt1, yInt1, xInt2, yInt2));
    }

    if (Math.abs(angle1 - Math.PI / 2) < 1e-10) {
      if (Math.abs(angle2 - Math.PI / 2) < 1e-10) {

      } else {
        buffer.put(x2 - Math.sin(angle1) * offset);
        buffer.put(y2 + Math.cos(angle1) * offset);
        buffer.put(x2 + Math.sin(angle1) * offset);
        buffer.put(y2 - Math.cos(angle1) * offset);
      }
    } else if (Math.abs(angle1 - angle2) > 1e-10) {
      buffer.put(xInt1);
      buffer.put(yInt1);
      buffer.put(xInt2);
      buffer.put(yInt2);
    } else {
      buffer.put(x2 - Math.sin(angle1) * offset);
      buffer.put(y2 + Math.cos(angle1) * offset);
      buffer.put(x2 + Math.sin(angle1) * offset);
      buffer.put(y2 - Math.cos(angle1) * offset);
    }
  }

  protected void connect5(double[] vertex) {
    if (secondLastPoint == null || stroke.getLineJoin() != BasicStroke.JOIN_MITER) {
      return;
    }

    double angle1 = angleOf(lastPoint, secondLastPoint);
    double angle2 = angleOf(vertex, lastPoint);

    double offset = stroke.getLineWidth() / 2;
    double cos1 = Math.cos(angle1) * offset;
    double sin1 = Math.sin(angle1) * offset;
    double cos2 = Math.cos(angle2) * offset;
    double sin2 = Math.sin(angle2) * offset;

    if (Arrays.equals(lastPoint, secondLastPoint)) {
      buffer.put(lastPoint[0] + sin2);
      buffer.put(lastPoint[1] - cos2);
      buffer.put(lastPoint[0] - sin2);
      buffer.put(lastPoint[1] + cos2);
      return;
    } else if (Arrays.equals(lastPoint, vertex)) {
      buffer.put(lastPoint[0] + sin1);
      buffer.put(lastPoint[1] - cos1);
      buffer.put(lastPoint[0] - sin1);
      buffer.put(lastPoint[1] + cos1);
      return;
    }

    double[] pt1 = new double[] { secondLastPoint[0] + sin1, secondLastPoint[1] - cos1 };
    double[] pt2 = new double[] { lastPoint[0] + sin2, lastPoint[1] - cos2 };
    double[] pt3 = new double[] { lastPoint[0] - secondLastPoint[0], lastPoint[1] - secondLastPoint[1] };
    double[] pt4 = new double[] { vertex[0] - lastPoint[0], vertex[1] - lastPoint[1] };
    double[] intersect = intersection(pt1, pt2, pt3, pt4);

    if (magnitude(pt3) > 0 && magnitude(pt4) > 0) {
      buffer.put(intersect[0]);
      buffer.put(intersect[1]);
    } else {
       System.out.println(Arrays.toString(intersect));
    }

    pt1 = new double[] { secondLastPoint[0] - sin1, secondLastPoint[1] + cos1 };
    pt2 = new double[] { lastPoint[0] - sin2, lastPoint[1] + cos2 };
    pt3 = new double[] { lastPoint[0] - secondLastPoint[0], lastPoint[1] - secondLastPoint[1] };
    pt4 = new double[] { vertex[0] - lastPoint[0], vertex[1] - lastPoint[1] };
    intersect = intersection(pt1, pt2, pt3, pt4);
    // System.out.println(String.format("(%.2f, %.2f) (%.2f, %.2f) (%.2f, %.2f) = (%.2f, %.2f)",
    // secondLastPoint[0], secondLastPoint[1],
    // lastPoint[0], lastPoint[1], vertex[0], vertex[1], intersect[0],
    // intersect[1]));
    if (magnitude(pt3) > 0 && magnitude(pt4) > 0) {
      buffer.put(intersect[0]);
      buffer.put(intersect[1]);
    } else {
      System.out.println(Arrays.toString(intersect));
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
    if (crossV[2] == 0) {
      // System.out.println(a);
    }

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

  protected void connect3(double[] vertex) {
    if (secondLastPoint == null || stroke.getLineJoin() != BasicStroke.JOIN_MITER) {
      return;
    }

    angleOf(lastPoint, secondLastPoint);
    double angle2 = angleOf(lastPoint, vertex);

    double x2 = lastPoint[0];
    double y2 = lastPoint[1];
    double offset = stroke.getLineWidth() / 2;
    double sin2 = Math.sin(angle2) * offset;
    // topleft
    buffer.put(x2 - offset);
    buffer.put(y2 - offset + sin2);

    // topright
    buffer.put(x2 + offset);
    buffer.put(y2 - offset);

    // bottomright
    buffer.put(x2 + offset);
    buffer.put(y2 + offset);

    // bottomleft
    buffer.put(x2 - offset);
    buffer.put(y2 + offset);
  }

  protected void connect1(double[] vertex) {
    if (secondLastPoint == null || stroke.getLineJoin() != BasicStroke.JOIN_MITER) {
      return;
    }

    double angle1 = angleOf(secondLastPoint, lastPoint);
    double angle2 = angleOf(lastPoint, vertex);
    if (Math.abs(Math.abs(angle1 - angle2) - Math.PI) < 0.1) {
      return;
    }

    double x1 = secondLastPoint[0];
    double y1 = secondLastPoint[1];
    double x2 = lastPoint[0];
    double y2 = lastPoint[1];
    double x3 = vertex[0];
    double y3 = vertex[1];
    double offset = stroke.getLineWidth() / 2;
    double a = offset / Math.abs(Math.cos(angle1));
    double b = offset / Math.abs(Math.cos(angle2));
    // System.out.println(angle1 + ", " + angle2);
    // System.out.println(a + ", " + b);
    double x, y;

    if (x1 == x2 || x2 == x3) {
      x = x2 + offset;
      y = y2 + offset;
    } else {
      x = ((x2 * x2 - x1 * x2) * y3 + (x1 * x2 - x2 * x3) * y2 + (x2 * x3 - x2 * x2) * y1 + ((-b - a) * x2 + (b + a) * x1) * x3
          + (b + a) * x2 * x2 + (-b - a) * x1 * x2)
          / ((x2 - x1) * y3 + (x1 - x3) * y2 + (x3 - x2) * y1);
      y = (y3 - y2) / (x3 - x2) * (x - x3) + y3 + b;
    }
    buffer.put(x);
    buffer.put(y);

    if (x1 == x2 || x2 == x3) {
      x = x2 - offset;
      y = y2 + offset;
    } else {
      a = -a;
      x = ((x2 * x2 - x1 * x2) * y3 + (x1 * x2 - x2 * x3) * y2 + (x2 * x3 - x2 * x2) * y1 + ((-b - a) * x2 + (b + a) * x1) * x3
          + (b + a) * x2 * x2 + (-b - a) * x1 * x2)
          / ((x2 - x1) * y3 + (x1 - x3) * y2 + (x3 - x2) * y1);
      y = (y3 - y2) / (x3 - x2) * (x - x3) + y3 + b;
    }
    buffer.put(x);
    buffer.put(y);

    if (x1 == x2 || x2 == x3) {
      x = x2 - offset;
      y = y2 - offset;
    } else {
      b = -b;
      x = ((x2 * x2 - x1 * x2) * y3 + (x1 * x2 - x2 * x3) * y2 + (x2 * x3 - x2 * x2) * y1 + ((-b - a) * x2 + (b + a) * x1) * x3
          + (b + a) * x2 * x2 + (-b - a) * x1 * x2)
          / ((x2 - x1) * y3 + (x1 - x3) * y2 + (x3 - x2) * y1);
      y = (y3 - y2) / (x3 - x2) * (x - x3) + y3 + b;
    }
    buffer.put(x);
    buffer.put(y);

    if (x1 == x2 || x2 == x3) {
      x = x2 + offset;
      y = y2 - offset;
    } else {
      a = -a;
      x = ((x2 * x2 - x1 * x2) * y3 + (x1 * x2 - x2 * x3) * y2 + (x2 * x3 - x2 * x2) * y1 + ((-b - a) * x2 + (b + a) * x1) * x3
          + (b + a) * x2 * x2 + (-b - a) * x1 * x2)
          / ((x2 - x1) * y3 + (x1 - x3) * y2 + (x3 - x2) * y1);
      y = (y3 - y2) / (x3 - x2) * (x - x3) + y3 + b;
    }
    buffer.put(x);
    buffer.put(y);
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
