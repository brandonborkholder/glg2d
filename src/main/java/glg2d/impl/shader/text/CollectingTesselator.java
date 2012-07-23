package glg2d.impl.shader.text;

import static java.util.Arrays.copyOf;
import glg2d.impl.AbstractTesselatorVisitor;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import com.jogamp.common.nio.Buffers;

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

  protected static int drawModeToIndex(int drawMode) {
    switch (drawMode) {
    case GL.GL_TRIANGLES:
      return 0;

    case GL.GL_TRIANGLE_STRIP:
      return 1;

    case GL.GL_TRIANGLE_FAN:
      return 2;

    default:
      return -1;
    }
  }

  protected static int indexToDrawMode(int index) {
    switch (index) {
    case 0:
      return GL.GL_TRIANGLES;

    case 1:
      return GL.GL_TRIANGLE_STRIP;

    case 2:
      return GL.GL_TRIANGLE_FAN;

    default:
      return -1;
    }
  }

  @Override
  public void beginPoly(int windingRule) {
    super.beginPoly(windingRule);

    for (int i = 0; i < indexBuffers.length; i++) {
      indexBuffers[i].clear();
      vertexBuffers[i].clear();
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

  protected void add(int index, FloatBuffer points) {
    // extend index buffer if necessary
    IntBuffer indexBuf = indexBuffers[index];
    if (indexBuf.position() == indexBuf.capacity() - 1) {
      IntBuffer newIndexBuf = IntBuffer.allocate(indexBuf.capacity() + 100);
      indexBuf.flip();
      newIndexBuf.put(indexBuf);
      indexBuf = newIndexBuf;
      indexBuffers[index] = indexBuf;
    }

    // extend vertex buffer if necessary
    int size = points.limit() - points.position();
    FloatBuffer vertBuf = vertexBuffers[index];
    if (vertBuf.position() >= vertBuf.limit() - size) {
      FloatBuffer newVertBuf = FloatBuffer.allocate(vertBuf.capacity() * 2);
      vertBuf.flip();
      newVertBuf.put(vertBuf);
      vertBuf = newVertBuf;
      vertexBuffers[index] = vertBuf;
    }

    // add the location of the start of this run
    indexBuf.put(vertBuf.position());
    vertBuf.put(points);
  }

  public TesselatedTriangles getTesselated() {
    for (int i = 0; i < indexBuffers.length; i++) {
      indexBuffers[i].flip();
      vertexBuffers[i].flip();
    }

    return new TesselatedTriangles(indexBuffers, vertexBuffers);
  }

  public static class TesselatedTriangles {
    private TriangleArray[] triangles;

    public TesselatedTriangles(IntBuffer[] indexBuffers, FloatBuffer[] vertexBuffers) {
      triangles = new TriangleArray[indexBuffers.length];
      int internalIndex = 0;

      for (int i = 0; i < indexBuffers.length; i++) {
        if (indexBuffers[i].position() < indexBuffers[i].limit()) {
          int drawMode = indexToDrawMode(i);
          triangles[internalIndex++] = new TriangleArray(drawMode, indexBuffers[i], vertexBuffers[i]);
        }
      }

      triangles = copyOf(triangles, internalIndex);
    }

    public void draw(GL2ES2 gl) {
      for (TriangleArray triangleArray : triangles) {
        triangleArray.draw(gl);
      }
    }
  }

  protected static class TriangleArray {
    private FloatBuffer triangles;
    private int[] offsets;
    private int drawMode;

    public TriangleArray(int drawMode, IntBuffer indexBuffer, FloatBuffer vertexBuffer) {
      this.drawMode = drawMode;

      int numPrimitives = indexBuffer.limit() - indexBuffer.position();
      int numVertices = vertexBuffer.limit() - vertexBuffer.position();

      offsets = new int[numPrimitives + 1];
      indexBuffer.get(offsets, 0, numPrimitives);
      offsets[numPrimitives] = numVertices;

      triangles = Buffers.newDirectFloatBuffer(numVertices);
      triangles.put(vertexBuffer);

      triangles.flip();
    }

    public void draw(GL2ES2 gl) {
      gl.glBufferData(GL.GL_ARRAY_BUFFER, triangles.limit(), triangles, GL2ES2.GL_STREAM_DRAW);

      System.out.print(drawMode);
      System.out.println(Arrays.toString(offsets));

      for (int i = 1; i < offsets.length; i++) {
        int from = offsets[i - 1];
        int to = offsets[i];

        gl.glDrawArrays(drawMode, from, to - from);
      }
    }
  }
}
