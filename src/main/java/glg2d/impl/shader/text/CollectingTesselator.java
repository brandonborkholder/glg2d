package glg2d.impl.shader.text;

import glg2d.impl.AbstractTesselatorVisitor;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

public class CollectingTesselator extends AbstractTesselatorVisitor {
  protected FloatBuffer[] vertexBuffers = new FloatBuffer[3];
  protected IntBuffer[] indexBuffers = new IntBuffer[vertexBuffers.length];

  public CollectingTesselator() {
    for (int i = 0; i < vertexBuffers.length; i++) {
      vertexBuffers[i] = FloatBuffer.allocate(4096);
      indexBuffers[i] = IntBuffer.allocate(100);
    }
  }
  
  @Override
  public void setGLContext(GL context) {
    // nop
  }

  protected int drawModeToIndex(int drawMode) {
    switch (drawMode) {
    case GL.GL_TRIANGLE_STRIP:
      return 0;

    case GL.GL_TRIANGLE_FAN:
      return 1;

    case GL.GL_TRIANGLES:
      return 2;

    default:
      return -1;
    }
  }

  protected int indexToDrawMode(int index) {
    switch (index) {
    case 0:
      return GL.GL_TRIANGLE_STRIP;

    case 1:
      return GL.GL_TRIANGLE_FAN;

    case 2:
      return GL.GL_TRIANGLES;

    default:
      return -1;
    }
  }

  protected void add(int index, FloatBuffer points) {
    // extend index buffer if necessary
    IntBuffer indexBuf = indexBuffers[index];
    if (indexBuf.position() == indexBuf.capacity() - 1) {
      IntBuffer newIndexBuf = IntBuffer.allocate(indexBuf.capacity() + 100);
      newIndexBuf.put(indexBuf);
      indexBuf = newIndexBuf;
      indexBuffers[index] = indexBuf;
    }

    // extend vertex buffer if necessary
    int size = points.limit() - points.position();
    FloatBuffer vertBuf = vertexBuffers[index];
    if (vertBuf.position() >= vertBuf.limit() - size) {
      FloatBuffer newVertBuf = FloatBuffer.allocate(vertBuf.capacity() * 2);
      newVertBuf.put(vertBuf);
      vertBuf = newVertBuf;
      vertexBuffers[index] = vertBuf;
    }

    indexBuf.put(vertBuf.position());
    vertBuf.put(points);
  }

  @Override
  public void beginPoly(int windingRule) {
    super.beginPoly(windingRule);

    for (int i = 0; i < indexBuffers.length; i++) {
      indexBuffers[i].clear();
      indexBuffers[i].put(vertexBuffers[i].position());
    }
  }

  @Override
  protected void endTess() {
    FloatBuffer buf = vBuffer.getBuffer();
    buf.flip();

    int index = drawModeToIndex(drawMode);
    add(index, buf);

    buf.clear();
  }

  public Triangles getTesselated() {
    for (IntBuffer indexBuffer : indexBuffers) {
      indexBuffer.flip();
    }

    Triangles triangles = new Triangles(indexBuffers);

    for (IntBuffer indexBuffer : indexBuffers) {
      indexBuffer.clear();
    }

    return triangles;
  }

  public class Triangles {
    private IntBuffer[] triangleIndexBuffers;
    private int[] offsets;

    Triangles(IntBuffer[] indexBuffers) {
      triangleIndexBuffers = new IntBuffer[indexBuffers.length];
      offsets = new int[indexBuffers.length];

      for (int i = 0; i < indexBuffers.length; i++) {
        if (indexBuffers[i].limit() > 0) {
          IntBuffer triangleIndexBuffer = IntBuffer.allocate(indexBuffers[i].limit() + 1);
          IntBuffer origIndexBuffer = indexBuffers[i];
          triangleIndexBuffers[i] = triangleIndexBuffer;

          int offset = origIndexBuffer.get();
          offsets[i] = offset;
          triangleIndexBuffer.put(0);

          while (origIndexBuffer.remaining() > 0) {
            triangleIndexBuffer.put(origIndexBuffer.get() - offset);
          }

          triangleIndexBuffer.put(vertexBuffers[i].position() - offset);
          triangleIndexBuffer.flip();
        }
      }
    }

    public void draw(GL2ES2 gl, int bufferId) {
      for (int i = 0; i < indexBuffers.length; i++) {
        if (triangleIndexBuffers[i] != null) {
          int drawMode = indexToDrawMode(i);
          fixFloatsBuffer(i);

          FloatBuffer verts = vertexBuffers[i];

          gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferId);
          gl.glBufferData(GL.GL_ARRAY_BUFFER, verts.limit() - verts.position(), verts, GL2ES2.GL_STREAM_DRAW);

          IntBuffer indexes = triangleIndexBuffers[i];
          gl.glDrawElements(drawMode, indexes.limit() - 1, GL.GL_UNSIGNED_INT, indexes);
        }
      }
    }

    private void fixFloatsBuffer(int index) {
      vertexBuffers[index].position(offsets[index]);
      int size = indexBuffers[index].get(indexBuffers[index].limit() - 1);
      vertexBuffers[index].limit(offsets[index] + size);
    }
  }
}
