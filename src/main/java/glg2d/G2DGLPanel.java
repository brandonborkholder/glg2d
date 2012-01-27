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

package glg2d;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Map;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLContext;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.RepaintManager;

/**
 * This panel redirects all paints to an OpenGL canvas. The drawable component
 * can be any JComponent with any number of children. Full repaints and partial
 * repaints (child components repainting) are all intercepted and painted using
 * the OpenGL canvas.
 */
public class G2DGLPanel extends G2DGLCanvas {
  private static final long serialVersionUID = -1078503678565053043L;

  GLGraphics2D g2d;

  /**
   * Returns the default, desired OpenGL capabilities needed for this component.
   */
  public static GLCapabilities getDefaultCapabalities() {
    return G2DGLCanvas.getDefaultCapabalities();
  }

  /**
   * Creates a new, blank {@code G2DGLPanel} using the default capabilities from
   * {@link #getDefaultCapabalities()}.
   */
  public G2DGLPanel() {
    this(getDefaultCapabalities());
  }

  /**
   * Creates a new, blank {@code G2DGLPanel} using the given OpenGL
   * capabilities.
   */
  public G2DGLPanel(GLCapabilities capabilities) {
    super(capabilities);
    RepaintManager.setCurrentManager(GLAwareRepaintManager.INSTANCE);
  }

  /**
   * Creates a new {@code G2DGLPanel} where {@code drawableComponent} fills the
   * canvas. This uses the default capabilities from
   * {@link #getDefaultCapabalities()}.
   */
  public G2DGLPanel(JComponent drawableComponent) {
    this();
    setDrawableComponent(drawableComponent);
  }

  /**
   * Creates a new {@code G2DGLPanel} where {@code drawableComponent} fills the
   * canvas.
   */
  public G2DGLPanel(GLCapabilities capabilities, JComponent drawableComponent) {
    this(capabilities);
    setDrawableComponent(drawableComponent);
  }

  @Override
  public void setDrawableComponent(JComponent component) {
    super.setDrawableComponent(component);

    if (component != null) {
      forceViewportToNativeDraw(component);
    }
  }

  /**
   * XXX This is a workaround until I figure out how to do blitting properly in
   * viewports.
   */
  private void forceViewportToNativeDraw(Container parent) {
    for (int i = 0; i < parent.getComponentCount(); i++) {
      Component c = parent.getComponent(i);
      if (c instanceof JViewport) {
        ((JViewport) c).setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
      }

      if (c instanceof Container) {
        forceViewportToNativeDraw((Container) c);
      }
    }
  }

  @Override
  protected GLAutoDrawable createGLComponent(GLCapabilitiesImmutable capabilities, GLContext shareWith) {
    GLJPanel canvas = new GLJPanel(capabilities, null, shareWith);
    canvas.setEnabled(false);
    return canvas;
  }

  @Override
  protected G2DGLEventListener createG2DListener(JComponent drawingComponent) {
    return new G2DGLEventListener(drawingComponent);
  }

  @Override
  public Graphics getGraphics() {
    return g2d == null ? super.getGraphics() : g2d.create();
  }

  @Override
  public void paint(Graphics g) {
    if (isGLDrawing() && getDrawableComponent() != null && canvas != null) {
      if (g2d == null) {
        ((G2DGLEventListener) g2dglListener).canvas = this;
        canvas.display();
      } else {
        getDrawableComponent().paint(g2d);
      }
    } else {
      super.paint(g);
    }
  }

  void paintGLImmediately(Map<JComponent, Rectangle> r) {
    ((G2DGLEventListener) g2dglListener).canvas = this;
    ((G2DGLEventListener) g2dglListener).repaints = r;
    canvas.display();
  }
}
