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

import glg2d.impl.BasicStrokeLineVisitor;

import java.awt.BasicStroke;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

public class GL2ES2StrokeLineVisitor extends BasicStrokeLineVisitor implements ShaderPathVisitor {
  protected GL2ES2 gl;

  protected SimpleStrokePipeline pipeline;
  
  protected float[] rgba;
  protected FloatBuffer glMatrixData;

  public GL2ES2StrokeLineVisitor(SimpleStrokePipeline pipeline) {
    this.pipeline = pipeline;
  }

  public GL2ES2StrokeLineVisitor() {
    this(new SimpleStrokePipeline());
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
    this.rgba = rgba;
  }

  @Override
  public void setStroke(BasicStroke stroke) {
    super.setStroke(stroke);
  }

  @Override
  public void setTransform(FloatBuffer glMatrixBuffer) {
    this.glMatrixData = glMatrixBuffer;
  }

  @Override
  public void beginPoly(int windingRule) {
    pipeline.use(gl, true);
    pipeline.setTransform(gl, glMatrixData);
    pipeline.setColor(gl, rgba);

    super.beginPoly(windingRule);
  }

  @Override
  public void endPoly() {
    super.endPoly();

    pipeline.use(gl, false);
  }

  @Override
  protected void drawBuffer(int mode) {
    FloatBuffer buf = vBuffer.getBuffer();
    if (buf.position() == 0) {
      return;
    }

    buf.flip();

    pipeline.draw(gl, buf, mode);

    vBuffer.clear();
  }
}
