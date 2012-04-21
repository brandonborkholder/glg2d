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

package glg2d;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

/**
 * Wraps a simple {@code FloatBuffer} and makes it easier to push 2-D vertices
 * into the buffer and then draw them using any mode desired. The default
 * constructor uses a global buffer since drawing in OpenGL is not
 * multi-threaded.
 */
public class VertexBuffer {
  protected static FloatBuffer globalBuffer = Buffers.newDirectFloatBuffer(300);

  protected static VertexBuffer shared = new VertexBuffer(globalBuffer);

  protected FloatBuffer buffer;

  /**
   * Creates a buffer that uses the shared global buffer. This is faster than
   * allocating multiple float buffers. Since OpenGL is single-threaded, we can
   * assume this won't be accessed outside the OpenGL thread and typically one
   * object is drawn completely before another one. If this is not true, one of
   * the objects being drawn simultaneously must use a private buffer. See
   * {@code #VertexBuffer(int)}.
   */
  public static VertexBuffer getSharedBuffer() {
    shared.clear();
    return shared;
  }

  protected VertexBuffer(FloatBuffer buffer) {
    this.buffer = buffer;
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
    this(Buffers.newDirectFloatBuffer(capacity * 2));
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
    int numFloats = numVertices * 2;
    ensureCapacity(numFloats);
    buffer.put(array, offset, numFloats);
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
    ensureCapacity(2);
    buffer.put(x);
    buffer.put(y);
  }
  
  protected void ensureCapacity(int numNewFloats) {
    if (buffer.capacity() <= buffer.position() + numNewFloats) {
      FloatBuffer larger = Buffers.newDirectFloatBuffer(buffer.position() * 2);
      int position = buffer.position();
      buffer.rewind();
      larger.put(buffer);
      buffer = larger;
      buffer.position(position);
    }
  }

  /**
   * Discard all existing points. This method is not necessary unless the points
   * already added are not needed anymore and the buffer will be reused.
   */
  public void clear() {
    buffer.rewind();
    buffer.limit(buffer.capacity());
  }

  public FloatBuffer getBuffer() {
    return buffer;
  }

  /**
   * Draws the vertices and rewinds the buffer to be ready to draw next time.
   *
   * @param gl
   *          The graphics context to use to draw
   * @param mode
   *          The mode, e.g. {@code GL#GL_LINE_STRIP}
   */
  public void drawBuffer(GL2 gl, int mode) {
    if (buffer.position() == 0) {
      return;
    }

    // use vertex arrays
    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

    int size = buffer.position() / 2;
    buffer.rewind();
    gl.glVertexPointer(2, GL2.GL_FLOAT, 0, buffer);
    gl.glDrawArrays(mode, 0, size);
  }
}
