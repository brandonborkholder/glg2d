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
package org.jogamp.glg2d;

import java.awt.Container;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.JComponent;
import javax.swing.RepaintManager;

public class GLAwareRepaintManager extends RepaintManager {
  public static RepaintManager INSTANCE = new GLAwareRepaintManager();

  @Override
  public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
    GLG2DCanvas canvas = getGLParent(c);
    if (canvas == null || c instanceof GLAutoDrawable) {
      super.addDirtyRegion(c, x, y, w, h);
    } else {
      canvas.repaint();
    }
  }

  protected GLG2DCanvas getGLParent(JComponent component) {
    Container c = component.getParent();
    while (true) {
      if (c == null) {
        return null;
      } else if (c instanceof GLG2DCanvas) {
        return (GLG2DCanvas) c;
      } else {
        c = c.getParent();
      }
    }
  }
}
