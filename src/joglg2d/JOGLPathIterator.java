package joglg2d;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
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
public class JOGLPathIterator {
  static final Ellipse2D.Double ELLIPSE = new Ellipse2D.Double();

  static final RoundRectangle2D.Double ROUND_RECT = new RoundRectangle2D.Double();

  static final Arc2D.Double ARC = new Arc2D.Double();

  protected final GL gl;

  protected double[] coords = new double[10];

  protected DoubleBuffer buffer = DoubleBuffer.allocate(50);

  public JOGLPathIterator(GL gl) {
    this.gl = gl;
  }

  public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight, boolean fill) {
    ROUND_RECT.setRoundRect(x, y, width, height, arcWidth, arcHeight);
    draw(ROUND_RECT, fill);
  }

  public void drawOval(int x, int y, int width, int height, boolean fill) {
    ELLIPSE.setFrame(x, y, width, height);
    draw(ELLIPSE, fill);
  }

  public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle, boolean fill) {
    ARC.setArc(arcAngle, x, y, width, height, startAngle, fill ? Arc2D.PIE : Arc2D.OPEN);
    draw(ARC, fill);
  }

  public void fill(Shape shape) {
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

      case PathIterator.SEG_CLOSE:
        glu.gluTessEndContour(tesselator);
      }
    }

    glu.gluTessEndPolygon(tesselator);
    glu.gluDeleteTess(tesselator);
  }

  public void draw(Shape shape, boolean fill) {
    if (fill) {
      fill(shape);
      return;
    }

    PathIterator iterator = shape.getPathIterator(null);

    double[] moveTo = new double[2];
    int index = 0;
    for (; !iterator.isDone(); iterator.next()) {
      switch (iterator.currentSegment(coords)) {
      case PathIterator.SEG_MOVETO:
        buffer.rewind();
        buffer.put(coords, 0, 2);
        System.arraycopy(coords, 0, moveTo, 0, 2);
        index = 2;
        break;

      case PathIterator.SEG_LINETO:
        buffer.put(coords, 0, 2);
        index += 2;
        break;

      case PathIterator.SEG_CLOSE:
        buffer.limit(index);
        gl.glBegin(fill ? GL.GL_POLYGON : GL.GL_LINE_LOOP);
        buffer.rewind();
        // hack until I can figure out vertex buffers
        while (buffer.hasRemaining()) {
          gl.glVertex2d(buffer.get(), buffer.get());
        }
        gl.glEnd();
        index = 0;
        buffer.limit(buffer.capacity());
        break;

      case PathIterator.SEG_QUADTO:

      }
    }

    if (index > 0) {
      assert !fill : "Invalid assumption";
      buffer.limit(index);
      gl.glBegin(GL.GL_LINES);
      gl.glVertex2dv(buffer);
      gl.glEnd();
      buffer.limit(buffer.capacity());
    }
  }
}
