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

package glg2d.impl.shader;

import glg2d.SimplePathVisitor;
import glg2d.VertexBuffer;

import java.awt.BasicStroke;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

public class ShaderLineVisitor extends SimplePathVisitor implements ShaderPathVisitor {
  protected VertexBuffer buffer = VertexBuffer.getSharedBuffer();

  protected BasicStroke stroke;
  protected float[] color;
  protected FloatBuffer matrixBuffer;

  protected float[] lastV = new float[2];

  protected GL2ES2 gl;

  protected GL2ES2StrokeLinePipeline pipeline;

  @Override
  public void setGLContext(GL context) {
    gl = context.getGL2ES2();
    if (pipeline != null) {
      pipeline.delete(gl);
    }
    // if (pipeline == null) {
    pipeline = new GL2ES2StrokeLinePipeline();
    pipeline.setup(gl);
    // }
  }

  @Override
  public void setColor(float[] rgba) {
    color = rgba;
  }

  @Override
  public void setTransform(FloatBuffer glMatrixBuffer) {
    matrixBuffer = glMatrixBuffer;
  }

  @Override
  public void setStroke(BasicStroke stroke) {
    this.stroke = stroke;
  }

  @Override
  public void moveTo(float[] vertex) {
    buffer.clear();

    buffer.addVertex(vertex, 0, 1);
    lastV[0] = vertex[0];
    lastV[1] = vertex[1];
  }

  @Override
  public void lineTo(float[] vertex) {
    // no 0-length lines
    if (vertex[0] == lastV[0] && vertex[1] == lastV[1]) {
      return;
    }

    buffer.addVertex(vertex, 0, 1);
    lastV[0] = vertex[0];
    lastV[1] = vertex[1];
  }

  @Override
  public void closeLine() {
    // add the first 2 vertices to wrap around the corner
    FloatBuffer buf = buffer.getBuffer();
    int oldPos = buf.position();
    buf.position(0);

    float[] first = new float[2];
    float[] second = new float[2];
    buf.get(first);
    buf.get(second);

    buf.position(oldPos);

    lineTo(first);
    lineTo(second);

    draw();
  }

  @Override
  public void beginPoly(int windingRule) {
    pipeline.use(gl, true);

    pipeline.setColor(gl, color);
    pipeline.setTransform(gl, matrixBuffer);
    pipeline.setStroke(gl, stroke);
  }

  @Override
  public void endPoly() {
    draw();
    pipeline.use(gl, false);
  }

  protected void draw() {
    FloatBuffer buf = buffer.getBuffer();
    if (buf.position() == 0) {
      return;
    }

    buf.flip();
    pipeline.draw(gl, buf);
  }
}
