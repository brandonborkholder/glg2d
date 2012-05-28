/*
 * Copyright 2012 Brandon Borkholder
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
package glg2d;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

public class GLAwareRepaintManager extends RepaintManager {
  public static RepaintManager INSTANCE = new GLAwareRepaintManager();

  private Map<JComponent, Rectangle> rects = new IdentityHashMap<JComponent, Rectangle>();

  private volatile boolean queued = false;

  @Override
  public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
    G2DGLPanel canvas = getGLParent(c);
    if (canvas == null || c instanceof GLAutoDrawable) {
      super.addDirtyRegion(c, x, y, w, h);
    } else {
      synchronized (rects) {
        if (!rects.containsKey(c)) {
          rects.put(c, new Rectangle(0, 0, c.getWidth(), c.getHeight()));
        }

        if (!queued && rects.size() > 0) {
          queued = true;
          queue();
        }
      }
    }
  }

  private void queue() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        Map<JComponent, Rectangle> r;
        synchronized (rects) {
          r = new IdentityHashMap<JComponent, Rectangle>(rects);
          queued = false;

          rects.clear();
        }

        r = filter(r);
        G2DGLPanel canvas = getGLParent(r.keySet().iterator().next());
        canvas.paintGLImmediately(r);
      }
    });
  }

  private Map<JComponent, Rectangle> filter(Map<JComponent, Rectangle> rects) {
    Iterator<JComponent> itr = rects.keySet().iterator();
    while (itr.hasNext()) {
      JComponent desc = itr.next();
      for (JComponent key : rects.keySet()) {
        if (desc != key && SwingUtilities.isDescendingFrom(desc, key)) {
          itr.remove();
          break;
        }
      }
    }

    return rects;
  }

  protected G2DGLPanel getGLParent(JComponent component) {
    Container c = component.getParent();
    while (true) {
      if (c == null) {
        return null;
      } else if (c instanceof G2DGLPanel) {
        return (G2DGLPanel) c;
      } else {
        c = c.getParent();
      }
    }
  }
}
