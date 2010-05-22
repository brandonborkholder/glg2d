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
    connect(secondPoint);
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

    connect(vertex);
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
    gl.glPointSize(2);
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

  protected void connect(double[] vertex) {
    if (secondLastPoint == null || stroke.getLineJoin() != BasicStroke.JOIN_MITER) {
      return;
    }

    double angle1 = angleOf(secondLastPoint, lastPoint);
    double angle2 = angleOf(lastPoint, vertex);
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
