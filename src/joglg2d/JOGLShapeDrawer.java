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
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/**
 * @author borkholder
 * @created Apr 20, 2010
 *
 */
public class JOGLShapeDrawer {
  static final Ellipse2D.Double ELLIPSE = new Ellipse2D.Double();

  static final RoundRectangle2D.Double ROUND_RECT = new RoundRectangle2D.Double();

  static final Arc2D.Double ARC = new Arc2D.Double();

  static final Rectangle2D.Double RECT = new Rectangle2D.Double();

  static final Line2D.Double LINE = new Line2D.Double();

  protected final GL gl;

  protected final GLU glu;

  protected PathVisitor tesselatingVisitor;

  protected PathVisitor simpleShapeFillVisitor;

  protected FastLineDrawingVisitor simpleStrokeVisitor;

  public JOGLShapeDrawer(GL gl) {
    this.gl = gl;
    glu = new GLU();
    tesselatingVisitor = new TesselatorVisitor(gl, glu);
    simpleShapeFillVisitor = new FillNonintersectingPolygonVisitor(gl);
    simpleStrokeVisitor = new FastLineDrawingVisitor(gl);
  }

  public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight, boolean fill, Stroke stroke) {
    ROUND_RECT.setRoundRect(x, y, width, height, arcWidth, arcHeight);
    if (fill) {
      fillPolygon(ROUND_RECT);
    } else {
      draw(ROUND_RECT, stroke);
    }
  }

  public void drawRect(int x, int y, int width, int height, boolean fill, Stroke stroke) {
    RECT.setRect(x, y, width, height);
    if (fill) {
      fillPolygon(RECT);
    } else {
      draw(RECT, stroke);
    }
  }

  public void drawLine(int x1, int y1, int x2, int y2, Stroke stroke) {
    LINE.setLine(x1, y1, x2, y2);
    draw(LINE, stroke);
  }

  public void drawOval(int x, int y, int width, int height, boolean fill, Stroke stroke) {
    ELLIPSE.setFrame(x, y, width, height);
    if (fill) {
      fillPolygon(ELLIPSE);
    } else {
      draw(ELLIPSE, stroke);
    }
  }

  public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle, boolean fill, Stroke stroke) {
    ARC.setArc(arcAngle, x, y, width, height, startAngle, fill ? Arc2D.PIE : Arc2D.OPEN);
    if (fill) {
      fillPolygon(ARC);
    } else {
      draw(ARC, stroke);
    }
  }

  public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints, Stroke stroke) {
    drawPoly(xPoints, yPoints, nPoints, false, false, stroke);
  }

  public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints, boolean fill, Stroke stroke) {
    drawPoly(xPoints, yPoints, nPoints, fill, true, stroke);
  }

  protected void drawPoly(int[] xPoints, int[] yPoints, int nPoints, boolean fill, boolean close, Stroke stroke) {
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
      draw(path, stroke);
    }
  }

  public void draw(Shape shape, Stroke stroke) {
    if (stroke instanceof BasicStroke) {
      BasicStroke basicStroke = (BasicStroke) stroke;
      if (basicStroke.getDashArray() == null) {
        simpleStrokeVisitor.setStroke(basicStroke);
        traceShape(shape, simpleStrokeVisitor);
        return;
      }
    }

    fill(stroke.createStrokedShape(shape));
  }

  public void fill(Shape shape) {
    // optimization for some basic shapes
    if (shape instanceof RectangularShape) {
      fillPolygon(shape);
      return;
    }

    traceShape(shape, tesselatingVisitor);
  }

  protected void fillPolygon(Shape shape) {
    traceShape(shape, simpleShapeFillVisitor);
  }

  protected void traceShape(Shape shape, PathVisitor visitor) {
    PathIterator iterator = shape.getPathIterator(null);
    visitor.beginPoly(iterator.getWindingRule());

    float[] coords = new float[10];
    float[] previousVertex = new float[2];
    for (; !iterator.isDone(); iterator.next()) {
      switch (iterator.currentSegment(coords)) {
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

      previousVertex[0] = coords[0];
      previousVertex[1] = coords[1];
    }

    visitor.endPoly();
  }
}
