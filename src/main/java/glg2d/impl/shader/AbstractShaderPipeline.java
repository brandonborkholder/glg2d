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
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2ES2;

import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;
import com.jogamp.opengl.util.glsl.ShaderState;

public abstract class AbstractShaderPipeline implements ShaderPipeline {
  protected ShaderCode vertexShader;
  protected ShaderCode fragmentShader;

  protected ShaderState shaderState;
  protected ShaderProgram program;

  @Override
  public void setup(GL2ES2 gl) {
    loadShaders(gl);
    createProgramAndAttach(gl);
  }

  @Override
  public boolean isSetup() {
    return program != null;
  }

  protected abstract void loadShaders(GL2ES2 gl);

  public void createProgramAndAttach(GL2ES2 gl) {
    program = new ShaderProgram();
    shaderState = new ShaderState();

    if (!program.add(gl, vertexShader, System.err)) {
      throw new ShaderException();
    }

    if (!program.add(gl, fragmentShader, System.err)) {
      throw new ShaderException();
    }

    if (!program.link(gl, System.err)) {
      throw new ShaderException();
    }

    shaderState.attachShaderProgram(gl, program);
  }

  @Override
  public void use(GL2ES2 gl, boolean use) {
    shaderState.useProgram(gl, use);
  }

  @Override
  public void delete(GL2ES2 gl) {
    program.destroy(gl);
    program = null;
  }

  protected String[] readShader(Class<?> context, String name) throws IOException {
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
      lines.add(line);
    }

    stream.close();
    return lines.toArray(new String[lines.size()]);
  }
}
