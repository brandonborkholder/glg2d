/**************************************************************************
   Copyright 2011 Brandon Borkholder

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

package glg2d.shaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

public class BasicShader implements Shader {
  protected String[] vertexShaderSrc;
  protected String[] fragmentShaderSrc;

  protected int vertexShaderId;
  protected int fragmentShaderId;

  protected GL gl;

  protected int programId;

  public BasicShader(GL gl) {
    this.gl = gl;
  }

  public void readSources(String name) {
    try {
      vertexShaderSrc = readShader(name + ".v");
      fragmentShaderSrc = readShader(name + ".f");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void createAndAttach() {
    programId = gl.glCreateProgram();
    gl.glAttachShader(programId, vertexShaderId);
    gl.glAttachShader(programId, fragmentShaderId);
    gl.glLinkProgram(programId);
    checkProgramThrowException(GL.GL_LINK_STATUS);
    
    gl.glValidateProgram(programId);
    checkProgramThrowException(GL.GL_VALIDATE_STATUS);
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

    vertexShaderId = gl.glCreateShader(GL.GL_VERTEX_SHADER);
    gl.glShaderSource(vertexShaderId, vertexShaderSrc.length, vertexShaderSrc, lengths, 0);
    gl.glCompileShader(vertexShaderId);
    
    checkShaderThrowException(vertexShaderId);
  }

  @Override
  public void compileFragmentShader() {
    int[] lengths = lengths(fragmentShaderSrc);

    fragmentShaderId = gl.glCreateShader(GL.GL_FRAGMENT_SHADER);
    gl.glShaderSource(fragmentShaderId, fragmentShaderSrc.length, fragmentShaderSrc, lengths, 0);
    gl.glCompileShader(fragmentShaderId);
    
    checkShaderThrowException(fragmentShaderId);
  }

  protected void checkShaderThrowException(int shader) {
    int[] result = new int[1];
    gl.glGetShaderiv(shader, GL.GL_COMPILE_STATUS, result, 0);
    if (result[0] == GL.GL_TRUE) {
      return;
    }
    
    gl.glGetShaderiv(shader, GL.GL_INFO_LOG_LENGTH, result, 0);
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
    
    gl.glGetProgramiv(programId, GL.GL_INFO_LOG_LENGTH, result, 0);
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

  protected String[] readShader(String name) throws IOException {
    InputStream stream = BasicShader.class.getResourceAsStream(name);
    if (stream == null) {
      throw new IOException("Shader " + name + " not found");
    }

    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    String line = null;
    List<String> lines = new ArrayList<String>();
    while ((line = reader.readLine()) != null) {
      lines.add(line);
    }

    return lines.toArray(new String[lines.size()]);
  }
}
