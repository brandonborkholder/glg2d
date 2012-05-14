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

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import com.jogamp.common.nio.Buffers;

public class SimpleStrokePipeline extends AbstractShaderPipeline {
  protected int transformLocation = -1;
  protected int colorLocation = -1;

  protected int vertCoordBuffer = -1;
  protected int vertCoordLocation = -1;

  public SimpleStrokePipeline() {
    this("StrokeLineShader.v", "FixedFuncShader.f");
  }

  public SimpleStrokePipeline(String vertexShaderFileName, String fragmentShaderFileName) {
    super(vertexShaderFileName, null, fragmentShaderFileName);
  }

  public void setColor(GL2ES2 gl, float[] rgba) {
    if (colorLocation >= 0) {
      gl.glUniform4fv(colorLocation, 1, rgba, 0);
    }
  }

  public void setTransform(GL2ES2 gl, FloatBuffer glMatrixData) {
    if (transformLocation >= 0) {
      gl.glUniformMatrix4fv(transformLocation, 1, false, glMatrixData);
    }
  }

  protected void bindBuffer(GL2ES2 gl, FloatBuffer vertexBuffer) {
    if (!gl.glIsBuffer(vertCoordBuffer)) {
      int[] ids = new int[1];
      gl.glGenBuffers(1, ids, 0);
      vertCoordBuffer = ids[0];
    }

    int count = vertexBuffer.limit() - vertexBuffer.position();
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertCoordBuffer);
    gl.glBufferData(GL.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT * count, vertexBuffer, GL2ES2.GL_STREAM_DRAW);

    gl.glVertexAttribPointer(vertCoordLocation, 2, GL.GL_FLOAT, false, 0, 0);
  }

  public void draw(GL2ES2 gl, FloatBuffer vertexBuffer, int mode) {
    int pos = vertexBuffer.position();
    int lim = vertexBuffer.limit();
    int numPts = (lim - pos) / 2;

    gl.glEnableVertexAttribArray(vertCoordLocation);

    bindBuffer(gl, vertexBuffer);

    gl.glDrawArrays(mode, 0, numPts);

    gl.glDisableVertexAttribArray(vertCoordLocation);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
  }

  @Override
  protected void setupUniformsAndAttributes(GL2ES2 gl) {
    super.setupUniformsAndAttributes(gl);

    transformLocation = gl.glGetUniformLocation(programId, "u_transform");
    colorLocation = gl.glGetUniformLocation(programId, "u_color");

    vertCoordLocation = gl.glGetAttribLocation(programId, "a_vertCoord");
  }
}
