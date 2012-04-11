/**************************************************************************
   Copyright 2012 Brandon Borkholder

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

package glg2d.impl;

import glg2d.GLG2DShapeHelper;
import glg2d.GLGraphics2D;
import glg2d.PathVisitor;

import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayDeque;
import java.util.Deque;

public abstract class AbstractShapeHelper implements GLG2DShapeHelper {
  /**
   * We know this is single-threaded, so we can use these as archetypes.
   */
  protected static final Ellipse2D.Float ELLIPSE = new Ellipse2D.Float();
  protected static final RoundRectangle2D.Float ROUND_RECT = new RoundRectangle2D.Float();
  protected static final Arc2D.Float ARC = new Arc2D.Float();
  protected static final Rectangle2D.Float RECT = new Rectangle2D.Float();
  protected static final Line2D.Float LINE = new Line2D.Float();

  protected Deque<Stroke> strokeStack = new ArrayDeque<Stroke>(10);

  @Override
  public void setG2D(GLGraphics2D g2d) {
    // nop
  }

  @Override
  public void push(GLGraphics2D newG2d) {
    strokeStack.push(newG2d.getStroke());
  }

  @Override
  public void pop(GLGraphics2D parentG2d) {
    strokeStack.pop();
  }

  @Override
  public void setHint(Key key, Object value) {
    // nop
  }

  @Override
  public void resetHints() {
    setHint(RenderingHints.KEY_ANTIALIASING, null);
  }

  @Override
  public void dispose() {
    // nop
  }

  @Override
  public void setStroke(Stroke stroke) {
    strokeStack.pop();
    strokeStack.push(stroke);
  }

  @Override
  public Stroke getStroke() {
    return strokeStack.peek();
  }

  @Override
  public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight, boolean fill) {
    ROUND_RECT.setRoundRect(x, y, width, height, arcWidth, arcHeight);
    if (fill) {
      fill(ROUND_RECT, true);
    } else {
      draw(ROUND_RECT);
    }
  }

  @Override
  public void drawRect(int x, int y, int width, int height, boolean fill) {
    RECT.setRect(x, y, width, height);
    if (fill) {
      fill(RECT, true);
    } else {
      draw(RECT);
    }
  }

  @Override
  public void drawLine(int x1, int y1, int x2, int y2) {
    LINE.setLine(x1, y1, x2, y2);
    draw(LINE);
  }

  @Override
  public void drawOval(int x, int y, int width, int height, boolean fill) {
    ELLIPSE.setFrame(x, y, width, height);
    if (fill) {
      fill(ELLIPSE, true);
    } else {
      draw(ELLIPSE);
    }
  }

  @Override
  public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle, boolean fill) {
    ARC.setArc(x, y, width, height, startAngle, arcAngle, fill ? Arc2D.PIE : Arc2D.OPEN);
    if (fill) {
      fill(ARC);
    } else {
      draw(ARC);
    }
  }

  @Override
  public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
    drawPoly(xPoints, yPoints, nPoints, false, false);
  }

  @Override
  public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints, boolean fill) {
    drawPoly(xPoints, yPoints, nPoints, fill, true);
  }

  protected void drawPoly(int[] xPoints, int[] yPoints, int nPoints, boolean fill, boolean close) {
    Path2D.Float path = new Path2D.Float(PathIterator.WIND_NON_ZERO, nPoints);
    path.moveTo(xPoints[0], yPoints[0]);
    for (int i = 1; i < nPoints; i++) {
      path.lineTo(xPoints[i], yPoints[i]);
    }

    if (close) {
      path.closePath();
    }

    if (fill) {
      fill(path);
    } else {
      draw(path);
    }
  }

  @Override
  public void fill(Shape shape) {
    fill(shape, false);
  }

  protected abstract void fill(Shape shape, boolean isDefinitelySimpleConvex);

  protected void traceShape(Shape shape, PathVisitor visitor) {
    PathIterator iterator = shape.getPathIterator(null);
    visitor.beginPoly(iterator.getWindingRule());

    float[] coords = new float[10];
    float[] previousVertex = new float[2];
    for (; !iterator.isDone(); iterator.next()) {
      int type = iterator.currentSegment(coords);
      switch (type) {
      case PathIterator.SEG_MOVETO:
        visitor.moveTo(coords);
        break;

      case PathIterator.SEG_LINETO:
        visitor.lineTo(coords);
        break;

      case PathIterator.SEG_QUADTO:
        visitor.quadTo(previousVertex, coords);
        break;

      case PathIterator.SEG_CUBICTO:
        visitor.cubicTo(previousVertex, coords);
        break;

      case PathIterator.SEG_CLOSE:
        visitor.closeLine();
        break;
      }

      switch (type) {
      case PathIterator.SEG_LINETO:
      case PathIterator.SEG_MOVETO:
        previousVertex[0] = coords[0];
        previousVertex[1] = coords[1];
        break;

      case PathIterator.SEG_QUADTO:
        previousVertex[0] = coords[2];
        previousVertex[1] = coords[3];
        break;

      case PathIterator.SEG_CUBICTO:
        previousVertex[0] = coords[4];
        previousVertex[1] = coords[5];
        break;
      }
    }

    visitor.endPoly();
  }
}