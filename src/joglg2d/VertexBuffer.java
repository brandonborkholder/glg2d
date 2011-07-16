package joglg2d;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

public class VertexBuffer {
  protected static FloatBuffer globalBuffer = BufferUtil.newFloatBuffer(300);

  protected FloatBuffer buffer;

  /**
   * Creates a buffer that uses the shared global buffer. This is faster than
   * allocating multiple float buffers. Since OpenGL is single-threaded, we can
   * assume this won't be accessed outside the OpenGL thread and typically one
   * object is drawn completely before another one. If this is not true, one of
   * the objects being drawn simultaneously must use a private buffer. See
   * {@code #VertexBuffer(int)}.
   */
  public VertexBuffer() {
    buffer = globalBuffer;
    clear();
  }

  /**
   * Creates a private buffer. This can be used without fear of clobbering the
   * global buffer. This should only be used if you have a need to create two
   * parallel shapes at the same time.
   * 
   * @param capacity
   *          The size of the buffer in number of vertices
   */
  public VertexBuffer(int capacity) {
    buffer = BufferUtil.newFloatBuffer(capacity * 2);
  }

  /**
   * Adds multiple vertices to the buffer.
   * 
   * @param array
   *          The array containing vertices in the form (x,y),(x,y)
   * @param offset
   *          The starting index
   * @param numVertices
   *          The number of vertices, pairs of floats
   */
  public void addVertex(float[] array, int offset, int numVertices) {
    for (int i = 0; i < numVertices; i++) {
      addVertex(array[offset + i], array[offset + i + 1]);
    }
  }

  /**
   * Adds a vertex to the buffer.
   * 
   * @param x
   *          The x coordinate
   * @param y
   *          The y coordinate
   */
  public void addVertex(float x, float y) {
    if (buffer.position() == buffer.capacity()) {
      FloatBuffer larger = BufferUtil.newFloatBuffer(buffer.position() * 2);
      buffer.rewind();
      larger.put(buffer);
      buffer = larger;
    }

    buffer.put(x);
    buffer.put(y);
  }

  /**
   * Discard all existing points. This method is not necessary unless the points
   * already added are not needed anymore and the buffer will be reused.
   */
  public void clear() {
    buffer.clear();
    buffer.rewind();
  }

  /**
   * Draws the vertices and rewinds the buffer to be ready to draw next time.
   * 
   * @param gl
   *          The graphics context to use to draw
   * @param mode
   *          The mode, e.g. {@code GL#GL_LINE_STRIP}
   */
  public void drawBuffer(GL gl, int mode) {
    if (buffer.position() == 0) {
      return;
    }

    int size = buffer.position() / 2;
    buffer.rewind();
    gl.glVertexPointer(2, GL.GL_FLOAT, 0, buffer);
    gl.glDrawArrays(mode, 0, size);
  }
}
