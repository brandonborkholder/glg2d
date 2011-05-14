/**************************************************************************
   Copyright 2010 Brandon Borkholder

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

package joglg2d;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLJPanel;
import javax.swing.JComponent;
import javax.swing.RepaintManager;

/**
 * @author borkholder
 * @created Feb 6, 2010
 */
@SuppressWarnings("serial")
public class JOGLPanel extends GLJPanel {
  public static GLCapabilities getDefaultCapabalities() {
    GLCapabilities caps = new GLCapabilities();
    caps.setRedBits(8);
    caps.setGreenBits(8);
    caps.setBlueBits(8);
    caps.setAlphaBits(8);
    caps.setDoubleBuffered(true);
    caps.setHardwareAccelerated(true);
    return caps;
  }

  public JOGLPanel() {
    this(getDefaultCapabalities());

    RepaintManager.setCurrentManager(new RepaintManager() {
      @Override
      public void addDirtyRegion(JComponent comp, int x, int y, int w, int h) {
        super.addDirtyRegion(comp, x, y, w, h);
        JComponent root = getRootJComponent(comp);
        // to avoid a recursive infinite loop
        if (comp != root) {
          super.addDirtyRegion(root, 0, 0, root.getWidth(), root.getHeight());
        }
      }

      public JComponent getRootJComponent(JComponent comp) {
        Component parent = comp.getParent();
        if (parent instanceof JComponent) {
          return getRootJComponent((JComponent) parent);
        }
        return comp;
      }
    });
  }

  public JOGLPanel(LayoutManager layout) {
    this();
    setLayout(layout);
  }

  public JOGLPanel(GLCapabilities caps) {
    super.setDoubleBuffered(false);
    addGLEventListener(new Graphics2DListener(this) {
      @Override
      protected void paintGL(JOGLG2D g2d) {
        JOGLPanel.this.paintGL(g2d);
        paintChildren(g2d);
      }
    });
  }

  @Override
  protected void paintChildren(Graphics g) {
    if (g instanceof JOGLG2D) {
      super.paintChildren(g);
    } else {
      // ignore
    }
  }

  /**
   * Double buffering is permanently off. Any value passed to this method is
   * ignored.
   */
  @Override
  public void setDoubleBuffered(boolean aFlag) {
  }

  @Override
  public boolean isDoubleBuffered() {
    return false;
  }

  protected void paintGL(JOGLG2D g2d) {
  }
}
