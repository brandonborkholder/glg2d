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
package org.jogamp.glg2d;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Map;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLContext;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JComponent;
import javax.swing.RepaintManager;

/**
 * This panel redirects all paints to an OpenGL canvas. The drawable component
 * can be any JComponent with any number of children. Full repaints and partial
 * repaints (child components repainting) are all intercepted and painted using
 * the OpenGL canvas.
 */
public class GLG2DPanel extends GLG2DCanvas {
  private static final long serialVersionUID = -1078503678565053043L;

  GLGraphics2D g2d;

  /**
   * Returns the default, desired OpenGL capabilities needed for this component.
   */
  public static GLCapabilities getDefaultCapabalities() {
    return GLG2DCanvas.getDefaultCapabalities();
  }

  /**
   * Creates a new, blank {@code G2DGLPanel} using the default capabilities from
   * {@link #getDefaultCapabalities()}.
   */
  public GLG2DPanel() {
    this(getDefaultCapabalities());
  }

  /**
   * Creates a new, blank {@code G2DGLPanel} using the given OpenGL
   * capabilities.
   */
  public GLG2DPanel(GLCapabilities capabilities) {
    super(capabilities);
    RepaintManager.setCurrentManager(GLAwareRepaintManager.INSTANCE);
  }

  /**
   * Creates a new {@code G2DGLPanel} where {@code drawableComponent} fills the
   * canvas. This uses the default capabilities from
   * {@link #getDefaultCapabalities()}.
   */
  public GLG2DPanel(JComponent drawableComponent) {
    this();
    setDrawableComponent(drawableComponent);
  }

  /**
   * Creates a new {@code G2DGLPanel} where {@code drawableComponent} fills the
   * canvas.
   */
  public GLG2DPanel(GLCapabilities capabilities, JComponent drawableComponent) {
    this(capabilities);
    setDrawableComponent(drawableComponent);
  }

  @Override
  protected GLAutoDrawable createGLComponent(GLCapabilitiesImmutable capabilities, GLContext shareWith) {
    GLJPanel canvas = new GLJPanel(capabilities, null, shareWith);
    canvas.setEnabled(false);
    return canvas;
  }

  @Override
  protected GLG2DEventListener createG2DListener(JComponent drawingComponent) {
    return new GLG2DEventListener(drawingComponent);
  }

  @Override
  public Graphics getGraphics() {
    return g2d == null ? super.getGraphics() : g2d.create();
  }

  @Override
  public void paint(Graphics g) {
    if (isGLDrawing() && getDrawableComponent() != null && canvas != null) {
      if (g2d == null) {
        // TODO: stack blown on UIDemo ComboBox if using canvas.display();
        /*
        ((G2DGLEventListener) g2dglListener).canvas = this;
        canvas.display();
        */
        getDrawableComponent().paint(g);
      } else {
        getDrawableComponent().paint(g2d);
      }
    } else {
      super.paint(g);
    }
  }

  void paintGLImmediately(Map<JComponent, Rectangle> r) {
    ((GLG2DEventListener) g2dglListener).canvas = this;
    ((GLG2DEventListener) g2dglListener).repaints = r;
    canvas.display();
  }
}
