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

package glg2d.shaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResourceShader extends AbstractShader {
  public ResourceShader(Class<?> context, String vertexName, String fragmentName) {
    try {
      vertexShaderSrc = readShader(context.getResourceAsStream(vertexName));
      fragmentShaderSrc = readShader(context.getResourceAsStream(fragmentName));
    } catch (IOException e) {
      throw new ShaderException(e);
    }
  }

  public ResourceShader(String vertexPath, String fragmentPath) {
    try {
      vertexShaderSrc = readShader(ResourceShader.class.getClassLoader().getResourceAsStream(vertexPath));
      fragmentShaderSrc = readShader(ResourceShader.class.getClassLoader().getResourceAsStream(fragmentPath));
    } catch (IOException e) {
      throw new ShaderException(e);
    }
  }

  protected String[] readShader(InputStream stream) throws IOException {
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
