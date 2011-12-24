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

package glg2d;

import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.RepaintManager;

public class GLAwareRepaintManager extends RepaintManager {
  public static RepaintManager INSTANCE = new GLAwareRepaintManager();

  @Override
  public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
    G2DGLCanvas glDrawable = getGLParent(c);
    if (glDrawable != null) {
      super.addDirtyRegion(glDrawable, 0, 0, glDrawable.getWidth(), glDrawable.getHeight());
    } else {
      super.addDirtyRegion(c, x, y, w, h);
    }
  }

  protected G2DGLCanvas getGLParent(JComponent component) {
    Container c = component;
    while (true) {
      if (c == null) {
        return null;
      } else if (c instanceof G2DGLCanvas) {
        return (G2DGLCanvas) c;
      } else {
        c = c.getParent();
      }
    }
  }
}
