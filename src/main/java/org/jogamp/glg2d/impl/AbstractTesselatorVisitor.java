/*
 * Copyright 2015 Brandon Borkholder
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
package org.jogamp.glg2d.impl;


import java.awt.BasicStroke;
import java.awt.geom.PathIterator;

import com.jogamp.opengl.GLException;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUtessellator;
import com.jogamp.opengl.glu.GLUtessellatorCallback;
import com.jogamp.opengl.glu.GLUtessellatorCallbackAdapter;

import org.jogamp.glg2d.VertexBuffer;

/**
 * Fills a shape by tesselating it with the GLU library. This is a slower
 * implementation and {@code FillNonintersectingPolygonVisitor} should be used
 * when possible.
 */
public abstract class AbstractTesselatorVisitor extends SimplePathVisitor {
  protected GLUtessellator tesselator;

  protected GLUtessellatorCallback callback;

  /**
   * Last command was a move to. This is where drawing starts.
   */
  protected float[] drawStart = new float[2];
  protected boolean drawing = false;

  protected int drawMode;
  protected VertexBuffer vBuffer = new VertexBuffer(1024);
  
  public AbstractTesselatorVisitor() {
    callback = new TessellatorCallback();
  }

  @Override
  public void setStroke(BasicStroke stroke) {
    // nop
  }

  @Override
  public void beginPoly(int windingRule) {
    tesselator = GLU.gluNewTess();
    configureTesselator(windingRule);

    GLU.gluTessBeginPolygon(tesselator, null);
  }

  protected void configureTesselator(int windingRule) {
    switch (windingRule) {
    case PathIterator.WIND_EVEN_ODD:
      GLU.gluTessProperty(tesselator, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_ODD);
      break;

    case PathIterator.WIND_NON_ZERO:
      GLU.gluTessProperty(tesselator, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_NONZERO);
      break;
    }

    GLU.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, callback);
    GLU.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, callback);
    GLU.gluTessCallback(tesselator, GLU.GLU_TESS_END, callback);
    GLU.gluTessCallback(tesselator, GLU.GLU_TESS_ERROR, callback);
    GLU.gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE, callback);
    GLU.gluTessNormal(tesselator, 0, 0, -1);

  }

  @Override
  public void moveTo(float[] vertex) {
    endIfRequired();
    drawStart[0] = vertex[0];
    drawStart[1] = vertex[1];
  }

  @Override
  public void lineTo(float[] vertex) {
    startIfRequired();
    addVertex(vertex);
  }

  private void addVertex(float[] vertex) {
    double[] v = new double[3];
    v[0] = vertex[0];
    v[1] = vertex[1];
    GLU.gluTessVertex(tesselator, v, 0, v);
  }

  @Override
  public void closeLine() {
    endIfRequired();
  }

  @Override
  public void endPoly() {
    // shapes may just end on the starting point without calling closeLine
    endIfRequired();

    GLU.gluTessEndPolygon(tesselator);
    GLU.gluDeleteTess(tesselator);
  }

  private void startIfRequired() {
    if (!drawing) {
      GLU.gluTessBeginContour(tesselator);
      addVertex(drawStart);
      drawing = true;
    }
  }

  private void endIfRequired() {
	if (drawing) {
      GLU.gluTessEndContour(tesselator);
      drawing = false;
    }
  }

  protected void beginTess(int type) {
    drawMode = type;
    vBuffer.clear();
  }

  protected void addTessVertex(double[] vertex) {
    vBuffer.addVertex((float) vertex[0], (float) vertex[1]);
  }

  protected abstract void endTess();

  protected class TessellatorCallback extends GLUtessellatorCallbackAdapter {
    @Override
    public void begin(int type) {
      beginTess(type);
    }

    @Override
    public void end() {
      endTess();
    }

    @Override
    public void vertex(Object vertexData) {
      assert vertexData instanceof double[] : "Invalid assumption";
      addTessVertex((double[]) vertexData);
    }

    @Override
    public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
      outData[0] = coords;
    }

    @Override
    public void error(int errnum) {
      throw new GLException("Tesselation Error: " + new GLU().gluErrorString(errnum));
    }
  }
}
