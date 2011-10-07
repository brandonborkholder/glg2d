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

package joglg2d;

import java.awt.Graphics;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.swing.JComponent;
import javax.swing.OverlayLayout;

public class G2DGLCanvas extends JComponent {
  private static final long serialVersionUID = -471481443599019888L;

  protected GLCanvas canvas;

  protected GLEventListener g2dglListener;

  protected JComponent drawableComponent;

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

  public G2DGLCanvas() {
    this(getDefaultCapabalities());
  }

  public G2DGLCanvas(GLCapabilities capabilities) {
    canvas = new GLCanvas(capabilities);
    setLayout(new OverlayLayout(this));
    add(canvas);
  }

  public G2DGLCanvas(JComponent drawableComponent) {
    this();
    setDrawableComponent(drawableComponent);
  }

  public G2DGLCanvas(GLCapabilities capabilities, JComponent drawableComponent) {
    this(capabilities);
    setDrawableComponent(drawableComponent);
  }

  public void setDrawableComponent(JComponent component) {
    if (component == drawableComponent) {
      return;
    }

    if (g2dglListener != null) {
      canvas.removeGLEventListener(g2dglListener);
    }

    if (drawableComponent != null) {
      remove(drawableComponent);
    }

    drawableComponent = component;
    if (drawableComponent != null) {
      g2dglListener = new G2DGLEventListener(drawableComponent);
      canvas.addGLEventListener(g2dglListener);
      add(drawableComponent);
    }
  }

  public JComponent getDrawableComponent() {
    return drawableComponent;
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    canvas.display();
  }

  @Override
  protected void paintChildren(Graphics g) {
    if (drawableComponent == null || !drawableComponent.isVisible()) {
      super.paintChildren(g);
    } else {
      // won't work, will continually call paint()
      drawableComponent.setVisible(false);
      super.paintChildren(g);
      drawableComponent.setVisible(true);
    }
  }
}
