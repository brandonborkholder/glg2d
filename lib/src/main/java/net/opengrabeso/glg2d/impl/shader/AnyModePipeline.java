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
package net.opengrabeso.glg2d.impl.shader;

import java.nio.FloatBuffer;

import com.github.opengrabeso.jaagl.GL2GL3;

import com.jogamp.common.nio.Buffers;

public class AnyModePipeline extends AbstractShaderPipeline {
  protected int vertCoordBuffer = -1;
  protected int vertCoordLocation = -1;
  protected int vertexArrayId = -1;

  public AnyModePipeline() {
    this("FixedFuncShader.v", "FixedFuncShader.f");
  }

  public AnyModePipeline(String vertexShaderFileName, String fragmentShaderFileName) {
    super(vertexShaderFileName, null, fragmentShaderFileName);
  }

  public void bindBuffer(GL2GL3 gl) {
      if (vertexArrayId < 0) {
          int[] vao = new int[]{0};
          gl.glGenVertexArrays(vao);
          vertexArrayId = vao[0];
      }

      gl.glBindVertexArray(vertexArrayId);

    gl.glEnableVertexAttribArray(vertCoordLocation);
    if (vertCoordBuffer < 0) {
        int[] ids = new int[1];
        gl.glGenBuffers(ids);
        vertCoordBuffer = ids[0];
    }

    gl.glBindBuffer(gl.GL_ARRAY_BUFFER(), vertCoordBuffer);
    gl.glVertexAttribPointer(vertCoordLocation, 2, gl.GL_FLOAT(), false, 0, 0);
  }

  public void bindBufferData(GL2GL3 gl, FloatBuffer vertexBuffer) {
    bindBuffer(gl);

    int count = vertexBuffer.limit() - vertexBuffer.position();
    gl.glBufferData(gl.GL_ARRAY_BUFFER(), Buffers.SIZEOF_FLOAT * count, vertexBuffer, gl.GL_STREAM_DRAW());
  }

  public void unbindBuffer(GL2GL3 gl) {
    gl.glDisableVertexAttribArray(vertCoordLocation);
    gl.glBindBuffer(gl.GL_ARRAY_BUFFER(), 0);
      gl.glBindVertexArray(0);
  }

  public void draw(GL2GL3 gl, int mode, FloatBuffer vertexBuffer) {
    bindBufferData(gl, vertexBuffer);

    int numPts = (vertexBuffer.limit() - vertexBuffer.position()) / 2;
    gl.glDrawArrays(mode, 0, numPts);

    unbindBuffer(gl);
  }

  @Override
  protected void setupUniformsAndAttributes(GL2GL3 gl) {
    super.setupUniformsAndAttributes(gl);

    transformLocation = gl.glGetUniformLocation(programId, "u_transform");
    colorLocation = gl.glGetUniformLocation(programId, "u_color");

    vertCoordLocation = gl.glGetAttribLocation(programId, "a_vertCoord");
  }

  @Override
  public void delete(GL2GL3 gl) {
    super.delete(gl);

    if (gl.glIsBuffer(vertCoordBuffer)) {
      gl.glDeleteBuffers(new int[] { vertCoordBuffer });
    }

    if (vertexArrayId >= 0) {
        gl.glDeleteVertexArrays(new int[]{vertexArrayId});
    }
  }
}
