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

import glg2d.GLGraphics2D;
import glg2d.impl.gl2.GL2ImageDrawer;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import javax.media.opengl.GL2ES2;

import com.jogamp.opengl.util.texture.Texture;

public class G2DShaderImageDrawer extends GL2ImageDrawer {
  protected Shader shader;

  public G2DShaderImageDrawer(Shader shader) {
    this.shader = shader;
  }

  @Override
  public void setG2D(GLGraphics2D g2d) {
    super.setG2D(g2d);

    GL2ES2 gl = g2d.getGLContext().getGL().getGL2ES2();
    if (!shader.isProgram(gl)) {
      shader.setup(gl);
    }
  }

  @Override
  protected void begin(Texture texture, AffineTransform xform, Color bgcolor) {
    super.begin(texture, xform, bgcolor);
    shader.use(true);
  }

  @Override
  protected void end(Texture texture) {
    shader.use(false);
    super.end(texture);
  }
}
