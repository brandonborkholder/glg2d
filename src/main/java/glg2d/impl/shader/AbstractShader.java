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

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

public abstract class AbstractShader implements Shader {
  protected String[] vertexShaderSrc;
  protected String[] fragmentShaderSrc;

  protected int vertexShaderId;
  protected int fragmentShaderId;

  protected GL2ES2 gl;

  protected int programId;

  @Override
  public void setup(GL2ES2 gl) {
    this.gl = gl;
    compileVertexShader();
    compileFragmentShader();
    createProgramAndAttach();
  }

  @Override
  public boolean isProgram(GL2ES2 gl) {
    return gl.glIsProgram(programId);
  }

  @Override
  public void createProgramAndAttach() {
    programId = gl.glCreateProgram();
    gl.glAttachShader(programId, vertexShaderId);
    gl.glAttachShader(programId, fragmentShaderId);
    gl.glLinkProgram(programId);
    checkProgramThrowException(GL2ES2.GL_LINK_STATUS);

    gl.glValidateProgram(programId);
    checkProgramThrowException(GL2ES2.GL_VALIDATE_STATUS);
  }

  @Override
  public void use(boolean use) {
    gl.glUseProgram(use ? programId : 0);
  }

  @Override
  public void delete() {
    gl.glDeleteShader(fragmentShaderId);
    gl.glDeleteShader(vertexShaderId);
    gl.glDeleteProgram(programId);
  }

  @Override
  public void compileVertexShader() {
    int[] lengths = lengths(vertexShaderSrc);

    vertexShaderId = gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER);
    gl.glShaderSource(vertexShaderId, vertexShaderSrc.length, vertexShaderSrc, lengths, 0);
    gl.glCompileShader(vertexShaderId);

    checkShaderThrowException(vertexShaderId);
  }

  @Override
  public void compileFragmentShader() {
    int[] lengths = lengths(fragmentShaderSrc);

    fragmentShaderId = gl.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);
    gl.glShaderSource(fragmentShaderId, fragmentShaderSrc.length, fragmentShaderSrc, lengths, 0);
    gl.glCompileShader(fragmentShaderId);

    checkShaderThrowException(fragmentShaderId);
  }

  protected void checkShaderThrowException(int shader) {
    int[] result = new int[1];
    gl.glGetShaderiv(shader, GL2ES2.GL_COMPILE_STATUS, result, 0);
    if (result[0] == GL.GL_TRUE) {
      return;
    }

    gl.glGetShaderiv(shader, GL2ES2.GL_INFO_LOG_LENGTH, result, 0);
    int size = result[0];
    byte[] data = new byte[size];
    gl.glGetShaderInfoLog(shader, size, result, 0, data, 0);

    String error = new String(data, 0, result[0]);
    throw new ShaderException(error);
  }

  protected void checkProgramThrowException(int status) {
    int[] result = new int[1];
    gl.glGetProgramiv(programId, status, result, 0);
    if (result[0] == GL.GL_TRUE) {
      return;
    }

    gl.glGetProgramiv(programId, GL2ES2.GL_INFO_LOG_LENGTH, result, 0);
    int size = result[0];
    byte[] data = new byte[size];
    gl.glGetProgramInfoLog(programId, size, result, 0, data, 0);

    String error = new String(data, 0, result[0]);
    throw new ShaderException(error);
  }

  protected int[] lengths(String[] lines) {
    int[] lengths = new int[lines.length];
    for (int i = 0; i < lengths.length; i++) {
      lengths[i] = lines[i].length();
    }

    return lengths;
  }
}
