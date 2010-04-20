package joglg2d;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;

/**
 * @author borkholder
 * @created Apr 20, 2010
 *
 */
public class JOGLPathIterator {
  static final Ellipse2D.Double ELLIPSE = new Ellipse2D.Double();

  protected final GL gl;

  protected float[] coords = new float[10];

  protected FloatBuffer buffer = FloatBuffer.allocate(50);

  public JOGLPathIterator(GL gl) {
    this.gl = gl;
  }

  public void drawOval(int x, int y, int width, int height, boolean fill) {
  }

  public void draw(Shape shape, boolean fill) {
    PathIterator iterator = shape.getPathIterator(null);

    iterator.getWindingRule();
    float[] moveTo = new float[2];
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
          gl.glVertex2f(buffer.get(), buffer.get());
        }
        gl.glEnd();
        index = 0;
        buffer.limit(buffer.capacity());
      }
    }

    if (index > 0) {
      assert !fill : "Invalid assumption";
      buffer.limit(index);
      gl.glBegin(GL.GL_LINES);
      gl.glVertex2fv(buffer);
      gl.glEnd();
      buffer.limit(buffer.capacity());
    }
  }
}
