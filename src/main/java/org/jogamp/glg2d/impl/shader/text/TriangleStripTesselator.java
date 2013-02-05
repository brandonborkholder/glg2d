/*
 * Copyright 2013 Brandon Borkholder
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
package org.jogamp.glg2d.impl.shader.text;

import static java.lang.Math.ceil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;

import javax.media.opengl.GL;

import org.jogamp.glg2d.impl.AbstractTesselatorVisitor;

public class TriangleStripTesselator extends AbstractTesselatorVisitor {
  protected Collection<TriangleStrip> triangleStrips;

  @Override
  public void setGLContext(GL context) {
    // nop
  }

  @Override
  public void beginPoly(int windingRule) {
    super.beginPoly(windingRule);

    vBuffer.clear();
    triangleStrips = new ArrayList<TriangleStrip>();
  }

  @Override
  protected void configureTesselator(int windingRule) {
    super.configureTesselator(windingRule);
  }

  @Override
  protected void endTess() {
    FloatBuffer buf = vBuffer.getBuffer();
    buf.flip();

    triangleStrips.addAll(getStrips(drawMode, buf));
    vBuffer.clear();
  }

  public Collection<TriangleStrip> getTesselated() {
    return triangleStrips;
  }

  protected Collection<TriangleStrip> getStrips(int drawMode, FloatBuffer vertexBuffer) {
    int size = vertexBuffer.limit() - vertexBuffer.position();
    Collection<TriangleStrip> strips = new ArrayList<TriangleStrip>();

    if (drawMode == GL.GL_TRIANGLE_STRIP) {
      float[] v = new float[size];
      vertexBuffer.get(v);
      strips.add(new TriangleStrip(v));
    } else if (drawMode == GL.GL_TRIANGLES) {
      float[] v = new float[6];
      while (vertexBuffer.remaining() >= 6) {
        vertexBuffer.get(v);
        strips.add(new TriangleStrip(v.clone()));
      }
    } else if (drawMode == GL.GL_TRIANGLE_FAN) {
      int newSize = (int) ceil((size - 2) / 4.0) + size;
      float[] v = new float[newSize];

      float origX = vertexBuffer.get();
      float origY = vertexBuffer.get();
      v[0] = origX;
      v[1] = origY;

      int index = 2;
      while (vertexBuffer.remaining() >= 4) {
        vertexBuffer.get(v, index, 4);
        index += 4;
        v[index++] = origX;
        v[index++] = origY;
      }

      if (vertexBuffer.remaining() >= 2) {
        vertexBuffer.get(v, index, 2);
        index += 2;
        v[index++] = origX;
        v[index++] = origY;
      }

      strips.add(new TriangleStrip(v));
    }

    return strips;
  }

  public static class TriangleStrip {
    public final float[] vertices;

    public TriangleStrip(float[] vertices) {
      this.vertices = vertices;
    }
  }
}
