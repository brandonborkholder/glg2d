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

import java.awt.BasicStroke;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import com.jogamp.common.nio.Buffers;

public class TriangleFanPolyFillShader extends AbstractShaderPipeline implements ShapeShaderPipeline {
  protected int colorLocation;
  protected int matrixLocation;

  protected int vertexBufferId;
  protected int vertCoordLocation;

  protected FloatBuffer centroidBuffer = FloatBuffer.allocate(2);

  public TriangleFanPolyFillShader() {
    this("FixedFuncShader.v", "FixedFuncShader.f");
  }

  public TriangleFanPolyFillShader(String vertexShaderFileName, String fragmentShaderFileName) {
    super(vertexShaderFileName, null, fragmentShaderFileName);
  }

  @Override
  public void setGLTransform(GL2ES2 gl, FloatBuffer glMatrixBuffer) {
    if (matrixLocation >= 0) {
      gl.glUniformMatrix4fv(matrixLocation, 1, false, glMatrixBuffer);
    }
  }

  @Override
  public void setColor(GL2ES2 gl, float[] rgba) {
    if (colorLocation >= 0) {
      gl.glUniform4fv(colorLocation, 1, rgba, 0);
    }
  }

  @Override
  public void setStroke(GL2ES2 gl, BasicStroke stroke) {
    // nop
  }

  @Override
  protected void setupUniformsAndAttributes(GL2ES2 gl) {
    super.setupUniformsAndAttributes(gl);

    colorLocation = -1;
    matrixLocation = -1;

    colorLocation = gl.glGetUniformLocation(programId, "u_color");
    matrixLocation = gl.glGetUniformLocation(programId, "u_transform");

    vertCoordLocation = gl.glGetAttribLocation(programId, "a_vertCoord");
  }

  protected void bufferData(GL2ES2 gl, FloatBuffer buffer) {
    if (!gl.glIsBuffer(vertexBufferId)) {
      int[] ids = new int[1];
      gl.glGenBuffers(1, ids, 0);
      vertexBufferId = ids[0];
    }

    int count = buffer.limit() - buffer.position();

    gl.glEnableVertexAttribArray(vertCoordLocation);

    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId);
    gl.glBufferData(GL.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT * (count + 2), null, GL2ES2.GL_STREAM_DRAW);
    gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0, Buffers.SIZEOF_FLOAT * 2, centroidBuffer);
    gl.glBufferSubData(GL.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT * 2, Buffers.SIZEOF_FLOAT * count, buffer);

    gl.glVertexAttribPointer(vertCoordLocation, 2, GL.GL_FLOAT, false, 0, 0);
  }

  protected void setupCentroid(FloatBuffer vertexBuffer) {
    int oldPos = vertexBuffer.position();

    float x = 0;
    float y = 0;

    while (vertexBuffer.position() < vertexBuffer.limit()) {
      x += vertexBuffer.get();
      y += vertexBuffer.get();
    }

    vertexBuffer.position(oldPos);
    float size = (vertexBuffer.limit() - vertexBuffer.position()) / 2;

    centroidBuffer.rewind();
    centroidBuffer.put(x / size);
    centroidBuffer.put(y / size);
    centroidBuffer.flip();
  }

  @Override
  public void draw(GL2ES2 gl, FloatBuffer vertexBuffer) {
    int count = vertexBuffer.limit() - vertexBuffer.position();
    setupCentroid(vertexBuffer);
    bufferData(gl, vertexBuffer);

    gl.glDrawArrays(GL.GL_TRIANGLE_FAN, 1, count / 2 + 1);

    gl.glDisableVertexAttribArray(vertCoordLocation);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
  }
}