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

import glg2d.G2DGLShapeDrawer;
import glg2d.GLGraphics2D;
import glg2d.PathVisitor;

import java.awt.Shape;

import javax.media.opengl.GL;

public class G2DShaderShapeDrawer extends G2DGLShapeDrawer {
  protected Shader shader;

  public G2DShaderShapeDrawer(Shader shader) {
    this.shader = shader;
  }

  @Override
  public void setG2D(GLGraphics2D g2d) {
    super.setG2D(g2d);

    GL gl = g2d.getGLContext().getGL();
    if (!shader.isProgram(gl)) {
      shader.setup(gl);
    }
  }

  @Override
  protected void traceShape(Shape shape, PathVisitor visitor) {
    shader.use(true);
    super.traceShape(shape, visitor);
    shader.use(false);
  }
}
