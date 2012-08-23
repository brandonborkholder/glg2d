/*
 * Copyright 2012 Brandon Borkholder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package glg2d.impl.shader.text;

import glg2d.impl.AbstractTesselatorVisitor;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.glu.GLU;

import com.jogamp.common.nio.Buffers;

public class CollectingTesselator extends AbstractTesselatorVisitor {
  @Override
  public void setGLContext(GL context) {
    // nop
  }

  @Override
  public void beginPoly(int windingRule) {
    super.beginPoly(windingRule);

    vBuffer.clear();
  }

  @Override
  protected void configureTesselator(int windingRule) {
    super.configureTesselator(windingRule);

    GLU.gluTessCallback(tesselator, GLU.GLU_TESS_EDGE_FLAG_DATA, callback);
  }

  @Override
  protected void beginTess(int type) {
    // don't clear the vertex buffer
  }

  @Override
  protected void endTess() {
    // nothing to do
  }

  public Triangles getTesselated() {
    FloatBuffer buf = vBuffer.getBuffer();
    buf.flip();
    return new Triangles(buf);
  }

  public static class Triangles {
    private FloatBuffer triangles;

    public Triangles(FloatBuffer vertexBuffer) {
      int numVertices = vertexBuffer.limit() - vertexBuffer.position();

      triangles = Buffers.newDirectFloatBuffer(numVertices);
      triangles.put(vertexBuffer);

      triangles.flip();
    }

    public void draw(GL2ES2 gl) {
      int numFloats = triangles.limit();
      gl.glBufferData(GL.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT * numFloats, triangles, GL2ES2.GL_STREAM_DRAW);
      gl.glDrawArrays(GL.GL_TRIANGLES, 0, numFloats / 2);
    }
  }
}
