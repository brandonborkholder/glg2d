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

public class GLShaderGraphics2D extends GLGraphics2D {
  public GLShaderGraphics2D(int width, int height) {
    super(width, height);
  }

  @Override
  protected void createDrawingHelpers() {
    Shader s = new ResourceShader(GLShaderGraphics2D.class, "TextureShader.v", "TextureShader.f");
    imageDrawer = new G2DShaderImageDrawer(s);
    stringDrawer = new G2DShaderStringDrawer(s);

    s = new ResourceShader(GLShaderGraphics2D.class, "FixedFuncShader.v", "FixedFuncShader.f");
    shapeDrawer = new G2DShaderShapeDrawer(s);

    addG2DDrawingHelper(imageDrawer);
    addG2DDrawingHelper(stringDrawer);
    addG2DDrawingHelper(shapeDrawer);
  }
}
