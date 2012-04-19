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

import com.jogamp.opengl.util.GLArrayDataServer;
import com.jogamp.opengl.util.glsl.ShaderCode;

public class GL2ES2ImagePipeline extends AbstractShaderPipeline {
  protected GLArrayDataServer vertArrayData;
  protected GLArrayDataServer texArrayData;

  protected int transformLocation;
  protected int colorLocation;
  protected int textureLocation;

  public void setMatrix(GL2ES2 gl, FloatBuffer glMatrixData) {
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

  public void bindVertCoords(GL2ES2 gl, FloatBuffer buffer) {
    vertArrayData.put(buffer);
    vertArrayData.seal(gl, true);
  }

  public void bindTexCoords(GL2ES2 gl, FloatBuffer buffer) {
    texArrayData.put(buffer);
    texArrayData.seal(gl, true);
  }

  @Override
  public void createProgramAndAttach(GL2ES2 gl) {
    super.createProgramAndAttach(gl);

    transformLocation = gl.glGetUniformLocation(program.id(), "u_transform");
    colorLocation = gl.glGetUniformLocation(program.id(), "u_color");
    textureLocation = gl.glGetUniformLocation(program.id(), "u_tex");

    vertArrayData = GLArrayDataServer.createGLSL("a_vertCoord", 2, GL.GL_FLOAT, false, 8, GL.GL_DYNAMIC_DRAW);
    texArrayData = GLArrayDataServer.createGLSL("a_texCoord", 2, GL.GL_FLOAT, false, 8, GL.GL_DYNAMIC_DRAW);
  }

  @Override
  protected void loadShaders(GL2ES2 gl) {
    vertexShader = ShaderCode.create(gl, GL2ES2.GL_VERTEX_SHADER, 1, getClass(), new String[] { "TextureShader.v" });
    fragmentShader = ShaderCode.create(gl, GL2ES2.GL_FRAGMENT_SHADER, 1, getClass(), new String[] { "TextureShader.f" });
  }

  public void draw(GL2ES2 gl) {
    gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4);
    vertArrayData.seal(gl, false);
    texArrayData.seal(gl, false);
  }
}
