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

import glg2d.GLGraphics2D;
import glg2d.impl.AbstractImageHelper;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;

import com.jogamp.opengl.util.texture.Texture;

public class G2DShaderImageDrawer extends AbstractImageHelper {
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
    /*
     * FIXME This is unexpected since we never disable blending, but in some
     * cases it interacts poorly with multiple split panes, scroll panes and the
     * text renderer to disable blending.
     */
    g2d.setComposite(g2d.getComposite());

    g2d.getMatrixHelper().push(g2d);
    g2d.getColorHelper().push(g2d);
    if (xform != null) {
      g2d.getMatrixHelper().transform(xform);
    }

    GL2ES2 gl = g2d.getGLContext().getGL().getGL2ES2();

    texture.enable(gl);
    texture.bind(gl);

    shader.use(true);
  }

  @Override
  protected void applyTexture(Texture texture, int dx1, int dy1, int dx2, int dy2, float sx1, float sy1, float sx2, float sy2) {
    GL2ES2 gl = g2d.getGLContext().getGL().getGL2ES2();
    
    // TODO this needs to be implemented using buffers

//    gl.glBegin(GL2.GL_QUADS);
//
//    // SW
//    gl.glTexCoord2f(sx1, sy2);
//    gl.glVertex2i(dx1, dy2);
//    // SE
//    gl.glTexCoord2f(sx2, sy2);
//    gl.glVertex2i(dx2, dy2);
//    // NE
//    gl.glTexCoord2f(sx2, sy1);
//    gl.glVertex2i(dx2, dy1);
//    // NW
//    gl.glTexCoord2f(sx1, sy1);
//    gl.glVertex2i(dx1, dy1);
//
//    gl.glEnd();
  }

  @Override
  protected void end(Texture texture) {
    shader.use(false);

    g2d.getMatrixHelper().pop(g2d);
    g2d.getColorHelper().pop(g2d);

    texture.disable(g2d.getGLContext().getGL());
  }
}
