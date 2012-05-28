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
package glg2d.impl.shader;

import glg2d.SimplePathVisitor;
import glg2d.VertexBuffer;

import java.awt.BasicStroke;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

public class TriangleFanSimplePolyFillVisitor extends SimplePathVisitor implements ShaderPathVisitor {
  protected GL2ES2 gl;

  protected VertexBuffer vBuffer = VertexBuffer.getSharedBuffer();

  protected float[] color;
  protected FloatBuffer glMatrixData;

  protected ShapeShaderPipeline pipeline;

  public TriangleFanSimplePolyFillVisitor() {
    this(new TriangleFanPolyFillShader());
  }

  public TriangleFanSimplePolyFillVisitor(ShapeShaderPipeline pipeline) {
    this.pipeline = pipeline;
  }

  @Override
  public void setGLContext(GL context) {
    gl = context.getGL2ES2();

    if (!pipeline.isSetup()) {
      pipeline.setup(gl);
    }
  }

  @Override
  public void setColor(float[] rgba) {
    color = rgba;
  }

  @Override
  public void setTransform(FloatBuffer glMatrixBuffer) {
    glMatrixData = glMatrixBuffer;
  }

  @Override
  public void setStroke(BasicStroke stroke) {
    // nop
  }

  @Override
  public void beginPoly(int windingRule) {
    // do we need to care about winding rule?
    vBuffer.clear();
    pipeline.use(gl, true);

    pipeline.setColor(gl, color);
    pipeline.setGLTransform(gl, glMatrixData);
  }

  @Override
  public void moveTo(float[] vertex) {
    draw();

    vBuffer.addVertex(vertex, 0, 1);
  }

  @Override
  public void lineTo(float[] vertex) {
    vBuffer.addVertex(vertex, 0, 1);
  }

  @Override
  public void closeLine() {
    FloatBuffer buf = vBuffer.getBuffer();
    float x = buf.get(0);
    float y = buf.get(1);
    vBuffer.addVertex(x, y);
  }

  @Override
  public void endPoly() {
    draw();
    pipeline.use(gl, false);
  }

  protected void draw() {
    FloatBuffer buf = vBuffer.getBuffer();
    if (buf.position() == 0) {
      return;
    }

    buf.flip();

    pipeline.draw(gl, buf);
    vBuffer.clear();
  }
}
