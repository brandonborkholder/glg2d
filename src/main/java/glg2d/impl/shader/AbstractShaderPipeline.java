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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;

import com.jogamp.common.nio.Buffers;

public abstract class AbstractShaderPipeline implements ShaderPipeline {
  protected int vertexShaderId = -1;
  protected int geometryShaderId = -1;
  protected int fragmentShaderId = -1;

  protected String vertexShaderFileName;
  protected String geometryShaderFileName;
  protected String fragmentShaderFileName;

  protected int programId = -1;

  public AbstractShaderPipeline(String vertexShaderFileName, String geometryShaderFileName, String fragmentShaderFileName) {
    this.vertexShaderFileName = vertexShaderFileName;
    this.geometryShaderFileName = geometryShaderFileName;
    this.fragmentShaderFileName = fragmentShaderFileName;
  }

  @Override
  public void setup(GL2ES2 gl) {
    createProgramAndAttach(gl);
    setupUniformsAndAttributes(gl);
  }

  @Override
  public boolean isSetup() {
    return programId != -1;
  }

  protected void createProgramAndAttach(GL2ES2 gl) {
    if (gl.glIsProgram(programId)) {
      delete(gl);
    }

    programId = gl.glCreateProgram();

    attachShaders(gl);

    gl.glLinkProgram(programId);
    checkProgramThrowException(gl, programId, GL2ES2.GL_LINK_STATUS);
  }

  protected void setupUniformsAndAttributes(GL2ES2 gl) {
    // nop
  }

  protected void attachShaders(GL2ES2 gl) {
    if (vertexShaderFileName != null) {
      vertexShaderId = compileShader(gl, GL2ES2.GL_VERTEX_SHADER, getClass(), vertexShaderFileName);
      gl.glAttachShader(programId, vertexShaderId);
    }

    if (geometryShaderFileName != null) {
      geometryShaderId = compileShader(gl, GL2GL3.GL_GEOMETRY_SHADER_ARB, getClass(), geometryShaderFileName);
      gl.glAttachShader(programId, geometryShaderId);
    }

    if (fragmentShaderFileName != null) {
      fragmentShaderId = compileShader(gl, GL2ES2.GL_FRAGMENT_SHADER, getClass(), fragmentShaderFileName);
      gl.glAttachShader(programId, fragmentShaderId);
    }
  }

  @Override
  public void use(GL2ES2 gl, boolean use) {
    gl.glUseProgram(use ? programId : 0);
  }

  @Override
  public void delete(GL2ES2 gl) {
    gl.glDeleteProgram(programId);
    deleteShaders(gl);

    programId = -1;
  }

  protected void deleteShaders(GL2ES2 gl) {
    if (gl.glIsShader(vertexShaderId)) {
      gl.glDeleteShader(vertexShaderId);
    }
    if (gl.glIsShader(geometryShaderId)) {
      gl.glDeleteShader(geometryShaderId);
    }
    if (gl.glIsShader(fragmentShaderId)) {
      gl.glDeleteShader(fragmentShaderId);
    }
  }

  protected int compileShader(GL2ES2 gl, int type, Class<?> context, String name) throws ShaderException {
    String[] source = readShader(context, name);
    int id = compileShader(gl, type, source);
    checkShaderThrowException(gl, id);
    return id;
  }

  protected int compileShader(GL2ES2 gl, int type, String[] source) throws ShaderException {
    int id = gl.glCreateShader(type);

    IntBuffer lineLengths = Buffers.newDirectIntBuffer(source.length);
    for (String element : source) {
      lineLengths.put(element.length());
    }

    lineLengths.rewind();
    gl.glShaderSource(id, source.length, source, lineLengths);
    int err = gl.glGetError();
    if (err != GL.GL_NO_ERROR) {
      throw new ShaderException("Shader source failed, GL Error: 0x" + Integer.toHexString(err));
    }

    gl.glCompileShader(id);
    if (err != GL.GL_NO_ERROR) {
      throw new ShaderException("Compile failed, GL Error: 0x" + Integer.toHexString(err));
    }

    return id;
  }

  protected String[] readShader(Class<?> context, String name) throws ShaderException {
    try {
      InputStream stream = null;
      if (context == null) {
        stream = AbstractShaderPipeline.class.getClassLoader().getResourceAsStream(name);
      } else {
        stream = context.getResourceAsStream(name);
      }

      if (stream == null) {
        throw new NullPointerException("InputStream is null");
      }

      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      String line = null;
      List<String> lines = new ArrayList<String>();
      while ((line = reader.readLine()) != null) {
        lines.add(line + "\n");
      }

      stream.close();
      return lines.toArray(new String[lines.size()]);
    } catch (IOException e) {
      throw new ShaderException("Error reading from stream", e);
    }
  }

  protected void checkShaderThrowException(GL2ES2 gl, int shaderId) {
    int[] result = new int[1];
    gl.glGetShaderiv(shaderId, GL2ES2.GL_COMPILE_STATUS, result, 0);
    if (result[0] == GL.GL_TRUE) {
      return;
    }

    gl.glGetShaderiv(shaderId, GL2ES2.GL_INFO_LOG_LENGTH, result, 0);
    int size = result[0];
    byte[] data = new byte[size];
    gl.glGetShaderInfoLog(shaderId, size, result, 0, data, 0);

    String error = new String(data, 0, result[0]);
    throw new ShaderException(error);
  }

  protected void checkProgramThrowException(GL2ES2 gl, int programId, int statusFlag) {
    int[] result = new int[1];
    gl.glGetProgramiv(programId, statusFlag, result, 0);
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
}
