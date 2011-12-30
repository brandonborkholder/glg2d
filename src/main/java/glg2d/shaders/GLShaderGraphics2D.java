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

import glg2d.GLGraphics2D;

import javax.media.opengl.GLAutoDrawable;

public class GLShaderGraphics2D extends GLGraphics2D {
  protected ShaderRegistry shaders;

  public GLShaderGraphics2D(int width, int height) {
    super(width, height);

    shaders = new ShaderRegistry();
    
    imageDrawer = new G2DShaderImageDrawer();
    stringDrawer = new G2DShaderStringDrawer();
  }

  @Override
  protected void setCanvas(GLAutoDrawable drawable) {
    glContext = drawable.getContext();
    gl = glContext.getGL();

    shaders.setG2D(this);
    super.setCanvas(drawable);
  }

  public ShaderRegistry getShaderRegistry() {
    return shaders;
  }
}
