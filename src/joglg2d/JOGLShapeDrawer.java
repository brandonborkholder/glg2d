package joglg2d;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.nio.DoubleBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallback;
import javax.media.opengl.glu.GLUtessellatorCallbackAdapter;

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

  protected double[] coords = new double[10];

  protected DoubleBuffer buffer = DoubleBuffer.allocate(50);

  public JOGLShapeDrawer(GL gl) {
    this.gl = gl;
  }

  public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight, boolean fill, Stroke stroke) {
    ROUND_RECT.setRoundRect(x, y, width, height, arcWidth, arcHeight);
    draw(ROUND_RECT, stroke, fill);
  }

  public void drawRect(int x, int y, int width, int height, boolean fill, Stroke stroke) {
    if (fill) {
      fillRect(x, y, width, height);
    } else {
      RECT.setRect(x, y, width, height);
      draw(RECT, stroke, fill);
    }
  }

  protected void fillRect(double x, double y, double width, double height) {
    gl.glBegin(GL.GL_QUADS);
    gl.glVertex2d(x, y);
    gl.glVertex2d(x, y + height);
    gl.glVertex2d(x + width, y + height);
    gl.glVertex2d(x + width, y);
    gl.glEnd();
  }

  public void drawLine(int x1, int y1, int x2, int y2, Stroke stroke) {
    LINE.setLine(x1, y1, x2, y2);
    draw(LINE, stroke, false);
  }

  public void drawOval(int x, int y, int width, int height, boolean fill, Stroke stroke) {
    ELLIPSE.setFrame(x, y, width, height);
    draw(ELLIPSE, stroke, fill);
  }

  public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle, boolean fill, Stroke stroke) {
    ARC.setArc(arcAngle, x, y, width, height, startAngle, fill ? Arc2D.PIE : Arc2D.OPEN);
    draw(ARC, stroke, fill);
  }

  public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints, Stroke stroke) {
    Path2D.Float path = new Path2D.Float(PathIterator.WIND_NON_ZERO, nPoints);
    path.moveTo(xPoints[0], yPoints[0]);
    for (int i = 1; i < nPoints; i++) {
      path.lineTo(xPoints[i], yPoints[i]);
    }

    fill(stroke.createStrokedShape(path));
  }

  public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints, boolean fill, Stroke stroke) {
    gl.glBegin(fill ? GL.GL_POLYGON : GL.GL_LINE_LOOP);
    for (int i = 0; i < nPoints; i++) {
      gl.glVertex2i(xPoints[i], yPoints[i]);
    }

    gl.glEnd();
  }

  private void draw(Shape shape, Stroke stroke, boolean fill) {
    if (fill) {
      fill(shape);
    } else {
      draw(shape, stroke);
    }
  }

  public void draw(Shape shape, Stroke stroke) {
    fill(stroke.createStrokedShape(shape));
  }

  public void fill(Shape shape) {
    // optimization for some basic shapes
    if (shape instanceof Rectangle2D) {
      Rectangle2D rect = (Rectangle2D) shape;
      fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
      return;
    }

    PathIterator iterator = shape.getPathIterator(null);
    final GLU glu = new GLU();
    GLUtessellator tesselator = glu.gluNewTess();
    GLUtessellatorCallback callback = new GLUtessellatorCallbackAdapter() {
      @Override
      public void begin(int type) {
        gl.glBegin(type);
      }

      @Override
      public void end() {
        gl.glEnd();
      }

      @Override
      public void vertex(Object vertexData) {
        if (vertexData instanceof double[]) {
          double[] v = (double[]) vertexData;
          gl.glVertex3d(v[0], v[1], v[2]);
        }
      }

      @Override
      public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
        outData[0] = coords;
      }

      @Override
      public void error(int errnum) {
        System.err.println("Tessellation Error: " + glu.gluErrorString(errnum));
      }
    };

    glu.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, callback);
    glu.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, callback);
    glu.gluTessCallback(tesselator, GLU.GLU_TESS_END, callback);
    glu.gluTessCallback(tesselator, GLU.GLU_TESS_ERROR, callback);
    glu.gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE, callback);

    switch (iterator.getWindingRule()) {
    case PathIterator.WIND_EVEN_ODD:
      glu.gluTessProperty(tesselator, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_ODD);
      break;

    case PathIterator.WIND_NON_ZERO:
      glu.gluTessProperty(tesselator, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_NONZERO);
      break;
    }

    glu.gluTessBeginPolygon(tesselator, null);

    double[] lastPoint = null;
    for (; !iterator.isDone(); iterator.next()) {
      double[] point = new double[3];
      switch (iterator.currentSegment(coords)) {
      case PathIterator.SEG_MOVETO:
        point[0] = coords[0];
        point[1] = coords[1];
        glu.gluTessBeginContour(tesselator);
        glu.gluTessVertex(tesselator, point, 0, point);
        break;

      case PathIterator.SEG_LINETO:
        point[0] = coords[0];
        point[1] = coords[1];
        glu.gluTessVertex(tesselator, point, 0, point);
        break;

      case PathIterator.SEG_QUADTO:
        double stepSize = 0.1;
        for (double i = 0; i <= 1; i += stepSize) {
          double[] p = new double[3];
          p[0] = (1 - i) * (1 - i) * lastPoint[0] + 2 * (1 - i) * i * coords[0] + i * i * coords[2];
          p[1] = (1 - i) * (1 - i) * lastPoint[1] + 2 * (1 - i) * i * coords[1] + i * i * coords[3];
          glu.gluTessVertex(tesselator, p, 0, p);
          point = p;
        }
        break;

      case PathIterator.SEG_CUBICTO:
        stepSize = 0.1;
        for (double i = 0; i <= 1; i += stepSize) {
          double[] p = new double[3];
          p[0] = (1 - i) * (1 - i) * (1 - i) * lastPoint[0] + 3 * (1 - i) * (1 - i) * i * coords[0] + 3 * (1 - i) * i * i * coords[2] + i
              * i * i * coords[4];
          p[1] = (1 - i) * (1 - i) * (1 - i) * lastPoint[1] + 3 * (1 - i) * (1 - i) * i * coords[1] + 3 * (1 - i) * i * i * coords[3] + i
              * i * i * coords[5];
          glu.gluTessVertex(tesselator, p, 0, p);
          point = p;
        }
        break;

      case PathIterator.SEG_CLOSE:
        glu.gluTessEndContour(tesselator);
        break;
      }

      lastPoint = point;
    }

    glu.gluTessEndPolygon(tesselator);
    glu.gluDeleteTess(tesselator);
  }
}
