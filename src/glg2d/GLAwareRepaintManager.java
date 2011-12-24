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
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.JComponent;
import javax.swing.RepaintManager;

public class GLAwareRepaintManager extends RepaintManager {
  public static RepaintManager INSTANCE = new GLAwareRepaintManager();

  private List<GLAutoDrawable> glDrawables = new ArrayList<GLAutoDrawable>();

  @Override
  public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
    GLAutoDrawable glDrawable = getGLParent(c);
    if (glDrawable != null) {
      addGLDirtyRegion(glDrawable, x, y, w, h);
    }

    super.addDirtyRegion(c, x, y, w, h);
  }

  private void addGLDirtyRegion(GLAutoDrawable glDrawable, int x, int y, int w, int h) {
    synchronized (glDrawables) {
      if (!glDrawables.contains(glDrawable)) {
        glDrawables.add(glDrawable);
      }
    }

    if (glDrawable instanceof JComponent) {
      super.addDirtyRegion((JComponent) glDrawable, 0, 0, glDrawable.getWidth(), glDrawable.getHeight());
    }
  }

  GLAutoDrawable getGLParent(JComponent component) {
    Container c = component;
    while (true) {
      if (c == null) {
        return null;
      } else if (c instanceof GLAutoDrawable) {
        return (GLAutoDrawable) c;
      }

      c = c.getParent();
    }
  }

  @Override
  public void paintDirtyRegions() {
    paintGLDirtyRegions();
    super.paintDirtyRegions();
  }

  public void paintGLDirtyRegions() {
    // all children should paint directly
    boolean isDoubleBuffered = isDoubleBufferingEnabled();
    setDoubleBufferingEnabled(false);

    List<GLAutoDrawable> drawables;
    synchronized (glDrawables) {
      drawables = new ArrayList<GLAutoDrawable>(glDrawables);
      glDrawables.clear();
    }

    for (GLAutoDrawable drawable : drawables) {
      drawable.display();
    }

    setDoubleBufferingEnabled(isDoubleBuffered);
  }
}
