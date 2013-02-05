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
package org.jogamp.glg2d.impl.gl2;


import java.awt.BasicStroke;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.jogamp.glg2d.VertexBuffer;
import org.jogamp.glg2d.impl.SimplePathVisitor;

/**
 * Fills a simple convex polygon. This class does not test to determine if the
 * polygon is actually simple and convex.
 */
public class FillSimpleConvexPolygonVisitor extends SimplePathVisitor {
  protected GL2 gl;

  protected VertexBuffer vBuffer = VertexBuffer.getSharedBuffer();

  @Override
  public void setGLContext(GL context) {
    gl = context.getGL2();
  }

  @Override
  public void setStroke(BasicStroke stroke) {
    // nop
  }

  @Override
  public void beginPoly(int windingRule) {
    vBuffer.clear();

    /*
     * We don't care what the winding rule is, we disable face culling.
     */
    gl.glDisable(GL.GL_CULL_FACE);
  }

  @Override
  public void closeLine() {
    vBuffer.drawBuffer(gl, GL2.GL_POLYGON);
  }

  @Override
  public void endPoly() {
  }

  @Override
  public void lineTo(float[] vertex) {
    vBuffer.addVertex(vertex, 0, 1);
  }

  @Override
  public void moveTo(float[] vertex) {
    vBuffer.clear();
    vBuffer.addVertex(vertex, 0, 1);
  }
}
