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
import com.jogamp.opengl.util.GLArrayDataServer;

public class GL2ES2ImagePipeline extends AbstractShaderPipeline {
  protected GLArrayDataServer vertArrayData;
  protected GLArrayDataServer texArrayData;

  protected int vertexBufferId;

  protected int transformLocation;
  protected int colorLocation;
  protected int textureLocation;
  protected int vertCoordLocation;
  protected int texCoordLocation;

  public GL2ES2ImagePipeline() {
    this("TextureShader.v", "TextureShader.f");
  }

  public GL2ES2ImagePipeline(String vertexShaderFileName, String fragmentShaderFileName) {
    super(vertexShaderFileName, null, fragmentShaderFileName);
  }

  public void setTransform(GL2ES2 gl, FloatBuffer glMatrixData) {
    if (transformLocation >= 0) {
      gl.glUniformMatrix4fv(transformLocation, 1, false, glMatrixData);
    }
  }

  public void setColor(GL2ES2 gl, float[] rgba) {
    if (colorLocation >= 0) {
      gl.glUniform4fv(colorLocation, 1, rgba, 0);
    }
  }

  public void setTextureUnit(GL2ES2 gl, int unit) {
    if (textureLocation >= 0) {
      gl.glUniform1i(textureLocation, unit);
    }
  }

  protected void bufferData(GL2ES2 gl, FloatBuffer buffer) {
    if (!gl.glIsBuffer(vertexBufferId)) {
      int[] ids = new int[1];
      gl.glGenBuffers(1, ids, 0);
      vertexBufferId = ids[0];
    }

    gl.glEnableVertexAttribArray(vertCoordLocation);
    gl.glEnableVertexAttribArray(texCoordLocation);

    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId);
    gl.glBufferData(GL.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT * 16, buffer, GL.GL_STATIC_DRAW);

    gl.glVertexAttribPointer(vertCoordLocation, 2, GL.GL_FLOAT, false, 4 * Buffers.SIZEOF_FLOAT, 0);
    gl.glVertexAttribPointer(texCoordLocation, 2, GL.GL_FLOAT, false, 4 * Buffers.SIZEOF_FLOAT, 2 * Buffers.SIZEOF_FLOAT);
  }

  public void draw(GL2ES2 gl, FloatBuffer interleavedVertTexBuffer) {
    bufferData(gl, interleavedVertTexBuffer);

    gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4);

    gl.glDisableVertexAttribArray(vertCoordLocation);
    gl.glDisableVertexAttribArray(texCoordLocation);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
  }

  @Override
  protected void setupUniformsAndAttributes(GL2ES2 gl) {
    super.setupUniformsAndAttributes(gl);

    transformLocation = gl.glGetUniformLocation(programId, "u_transform");
    colorLocation = gl.glGetUniformLocation(programId, "u_color");
    textureLocation = gl.glGetUniformLocation(programId, "u_tex");

    vertCoordLocation = gl.glGetAttribLocation(programId, "a_vertCoord");
    texCoordLocation = gl.glGetAttribLocation(programId, "a_texCoord");
  }

  @Override
  public void delete(GL2ES2 gl) {
    super.delete(gl);

    if (gl.glIsBuffer(vertexBufferId)) {
      gl.glDeleteBuffers(1, new int[] { vertexBufferId }, 0);
    }
  }
}
