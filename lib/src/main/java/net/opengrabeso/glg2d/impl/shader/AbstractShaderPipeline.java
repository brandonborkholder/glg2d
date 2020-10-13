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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.github.opengrabeso.jaagl.GL;
import com.github.opengrabeso.jaagl.GL2GL3;

public abstract class AbstractShaderPipeline implements ShaderPipeline {
  protected int vertexShaderId = 0;
  protected int geometryShaderId = 0;
  protected int fragmentShaderId = 0;

  protected String vertexShaderFileName;
  protected String geometryShaderFileName;
  protected String fragmentShaderFileName;

  protected int programId = 0;
  protected int transformLocation = -1;
  protected int colorLocation = -1;

  public AbstractShaderPipeline(String vertexShaderFileName, String geometryShaderFileName, String fragmentShaderFileName) {
    this.vertexShaderFileName = vertexShaderFileName;
    this.geometryShaderFileName = geometryShaderFileName;
    this.fragmentShaderFileName = fragmentShaderFileName;
  }

  @Override
  public void setup(GL2GL3 gl) {
    createProgramAndAttach(gl);
    setupUniformsAndAttributes(gl);
  }

  @Override
  public boolean isSetup() {
    return programId > 0;
  }

  public void setColor(GL2GL3 gl, float[] rgba) {
    if (colorLocation >= 0) {
      gl.glUniform4fv(colorLocation, 1, rgba, 0);
    }
  }

  public void setTransform(GL2GL3 gl, float[] glMatrixData) {
    if (transformLocation >= 0) {
      gl.glUniformMatrix4fv(transformLocation, 1, false, glMatrixData, 0);
    }
  }

  protected void createProgramAndAttach(GL2GL3 gl) {
    if (gl.glIsProgram(programId)) {
      delete(gl);
    }

    programId = gl.glCreateProgram();

    attachShaders(gl);

    gl.glLinkProgram(programId);
    checkProgramThrowException(gl, programId, gl.GL_LINK_STATUS());
  }

  protected void setupUniformsAndAttributes(GL2GL3 gl) {
    // nop
  }

  protected void attachShaders(GL2GL3 gl) {
    if (vertexShaderFileName != null) {
      vertexShaderId = compileShader(gl, gl.GL_VERTEX_SHADER(), getClass(), vertexShaderFileName);
      gl.glAttachShader(programId, vertexShaderId);
    }

    if (geometryShaderFileName != null) {
      geometryShaderId = compileShader(gl, gl.GL_GEOMETRY_SHADER(), getClass(), geometryShaderFileName);
      gl.glAttachShader(programId, geometryShaderId);
    }

    if (fragmentShaderFileName != null) {
      fragmentShaderId = compileShader(gl, gl.GL_FRAGMENT_SHADER(), getClass(), fragmentShaderFileName);
      gl.glAttachShader(programId, fragmentShaderId);
    }
  }

  @Override
  public void use(GL2GL3 gl, boolean use) {
    gl.glUseProgram(use ? programId : 0);
  }

  @Override
  public void delete(GL2GL3 gl) {
    gl.glDeleteProgram(programId);
    deleteShaders(gl);

    programId = 0;
  }

  protected void deleteShaders(GL2GL3 gl) {
    if (vertexShaderId > 0) {
      gl.glDeleteShader(vertexShaderId);
      vertexShaderId = 0;
    }
    if (geometryShaderId > 0) {
      gl.glDeleteShader(geometryShaderId);
      geometryShaderId = 0;
    }
    if (fragmentShaderId > 0) {
      gl.glDeleteShader(fragmentShaderId);
      fragmentShaderId = 0;
    }
  }

  protected int compileShader(GL2GL3 gl, int type, Class<?> context, String name) throws ShaderException {
    String source = readShader(context, name);
    int id = compileShader(gl, type, source);
    checkShaderThrowException(gl, id);
    return id;
  }

  protected int compileShader(GL2GL3 gl, int type, String source) throws ShaderException {
    int id = gl.glCreateShader(type);

    gl.glShaderSource(id, source);
    int err = gl.glGetError();
    if (err != gl.GL_NO_ERROR()) {
      throw new ShaderException("Shader source failed, GL Error: 0x" + Integer.toHexString(err));
    }

    gl.glCompileShader(id);
    if (err != gl.GL_NO_ERROR()) {
      throw new ShaderException("Compile failed, GL Error: 0x" + Integer.toHexString(err));
    }

    return id;
  }

  protected String readShader(Class<?> context, String name) throws ShaderException {
    try {
      InputStream stream = null;
      if (context != null) {
        stream = context.getResourceAsStream(name);
      }
      
      if (stream == null) {
        stream = AbstractShaderPipeline.class.getResourceAsStream(name);
      }

      if (stream == null) {
        stream = AbstractShaderPipeline.class.getClassLoader().getResourceAsStream(name);
      }

      if (stream == null) {
        throw new NullPointerException("InputStream for " + name + " is null");
      }

      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      String line = null;
      StringBuilder sb = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
      }

      stream.close();
      return sb.toString();
    } catch (IOException e) {
      throw new ShaderException("Error reading from stream", e);
    }
  }

  protected void checkShaderThrowException(GL2GL3 gl, int shaderId) {
    int[] result = new int[1];
    gl.glGetShaderiv(shaderId, gl.GL_COMPILE_STATUS(), result, 0);
    if (result[0] == gl.GL_TRUE()) {
      return;
    }

    String error = gl.glGetShaderInfoLog(shaderId);

    throw new ShaderException(error);
  }

  protected void checkProgramThrowException(GL2GL3 gl, int programId, int statusFlag) {
    int[] result = new int[1];
    gl.glGetProgramiv(programId, statusFlag, result, 0);
    if (result[0] == gl.GL_TRUE()) {
      return;
    }

    String error = gl.glGetProgramInfoLog(programId);
    throw new ShaderException(error);
  }
}
