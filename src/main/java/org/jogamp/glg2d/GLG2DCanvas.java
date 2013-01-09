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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager2;
import java.io.Serializable;
import java.util.logging.Logger;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.Threading;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;

/**
 * This canvas redirects all paints to an OpenGL canvas. The drawable component
 * can be any JComponent. This is a simple implementation to allow manual
 * painting of a JComponent scene to OpenGL. If you want to intercept Swing
 * repaints in child components, use {@link GLG2DPanel}. A {@code G2DGLCanvas}
 * is more appropriate when rendering a complex scene using
 * {@link JComponent#paintComponent(Graphics)} and the {@code Graphics2D}
 * object. If the drawable component contains child components, use
 * {@link GLG2DPanel}.
 * 
 * <p>
 * GL drawing can be enabled or disabled using the {@code setGLDrawing(boolean)}
 * method. If GL drawing is enabled, all full paint requests are intercepted and
 * the drawable component is drawn to the OpenGL canvas.
 * </p>
 * 
 * <p>
 * Override {@link #createGLComponent(GLCapabilitiesImmutable, GLContext)} to
 * create the OpenGL canvas. The returned canvas may be a {@code GLJPanel} or a
 * {@code GLCanvas}. {@link #createG2DListener(JComponent)} is used to create
 * the {@code GLEventListener} that will draw to the OpenGL canvas.
 * </p>
 */
public class GLG2DCanvas extends JComponent {
  private static final long serialVersionUID = -471481443599019888L;

  protected GLAutoDrawable canvas;
  protected GLCapabilitiesImmutable chosenCapabilities;

  protected GLEventListener g2dglListener;

  /**
   * @see #removeNotify()
   */
  private GLAutoDrawable sideContext;

  private JComponent drawableComponent;

  private boolean drawGL;

  /**
   * Returns the default, desired OpenGL capabilities needed for this component.
   */
  public static GLCapabilities getDefaultCapabalities() {
    GLCapabilities caps = new GLCapabilities(GLProfile.getGL2ES2());
    caps.setRedBits(8);
    caps.setGreenBits(8);
    caps.setBlueBits(8);
    caps.setAlphaBits(8);
    caps.setDoubleBuffered(true);
    caps.setHardwareAccelerated(true);
    caps.setNumSamples(4);
    caps.setBackgroundOpaque(false);
    caps.setSampleBuffers(true);
    return caps;
  }

  /**
   * Creates a new, blank {@code G2DGLCanvas} using the default capabilities
   * from {@link #getDefaultCapabalities()}.
   */
  public GLG2DCanvas() {
    this(getDefaultCapabalities());
  }

  /**
   * Creates a new, blank {@code G2DGLCanvas} using the given OpenGL
   * capabilities.
   */
  public GLG2DCanvas(GLCapabilities capabilities) {
    canvas = createGLComponent(capabilities, null);

    /*
     * Set both canvas and drawableComponent to be the same size, but we never
     * draw the drawableComponent except into the canvas.
     */
    setLayout(new GLOverlayLayout());
    add((Component) canvas);

    setGLDrawing(true);
  }

  /**
   * Creates a new {@code G2DGLCanvas} where {@code drawableComponent} fills the
   * canvas. This uses the default capabilities from
   * {@link #getDefaultCapabalities()}.
   */
  public GLG2DCanvas(JComponent drawableComponent) {
    this();
    setDrawableComponent(drawableComponent);
  }

  /**
   * Creates a new {@code G2DGLCanvas} where {@code drawableComponent} fills the
   * canvas.
   */
  public GLG2DCanvas(GLCapabilities capabilities, JComponent drawableComponent) {
    this(capabilities);
    setDrawableComponent(drawableComponent);
  }

  /**
   * Returns {@code true} if the {@code drawableComonent} is drawn using OpenGL
   * libraries. If {@code false}, it is using normal Java2D drawing routines.
   */
  public boolean isGLDrawing() {
    return drawGL;
  }

  /**
   * Sets the drawing path, {@code true} for OpenGL, {@code false} for normal
   * Java2D.
   * 
   * @see #isGLDrawing()
   */
  public void setGLDrawing(boolean drawGL) {
    if (this.drawGL != drawGL) {
      this.drawGL = drawGL;
      ((Component) canvas).setVisible(drawGL);
      setOpaque(drawGL);

      firePropertyChange("gldrawing", !drawGL, drawGL);

      repaint();
    }
  }

  /**
   * Gets the {@code JComponent} to be drawn to the OpenGL canvas.
   */
  public JComponent getDrawableComponent() {
    return drawableComponent;
  }

  /**
   * Sets the {@code JComponent} that will be drawn to the OpenGL canvas.
   */
  public void setDrawableComponent(JComponent component) {
    if (component == drawableComponent) {
      return;
    }

    if (g2dglListener != null) {
      canvas.removeGLEventListener(g2dglListener);
      if (sideContext != null) {
        sideContext.removeGLEventListener(g2dglListener);
      }
    }

    if (drawableComponent != null) {
      remove(drawableComponent);
    }

    drawableComponent = component;
    if (drawableComponent != null) {
      verifyHierarchy(drawableComponent);

      g2dglListener = createG2DListener(drawableComponent);
      canvas.addGLEventListener(g2dglListener);
      if (sideContext != null) {
        sideContext.addGLEventListener(g2dglListener);
      }

      add(drawableComponent);
    }
  }

  /**
   * Checks the component and all children to ensure that everything is pure
   * Swing. We can only draw lightweights.
   * 
   * 
   * We'll also set PopupMenus to heavyweight and fix JViewport blitting.
   */
  protected void verifyHierarchy(Component comp) {
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);

    if (!(comp instanceof JComponent)) {
      Logger.getLogger(GLG2DCanvas.class.getName()).warning("Drawable component and children should be pure Swing: " +
          comp + " does not inherit JComponent");
    }

    if (comp instanceof JViewport) {
      ((JViewport) comp).setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
    }

    if (comp instanceof Container) {
      Container cont = (Container) comp;
      for (int i = 0; i < cont.getComponentCount(); i++) {
        verifyHierarchy(cont.getComponent(i));
      }
    }
  }

  /**
   * Creates a {@code Component} that is also a {@code GLAutoDrawable}. This is
   * where all the drawing takes place. The advantage of a {@code GLCanvas} is
   * that it is faster, but a {@code GLJPanel} is more portable. The component
   * should also be disabled so that it does not receive events that should be
   * sent to the {@code drawableComponent}. A {@code GLCanvas} is a heavyweight
   * component and on some platforms will not pass through mouse events even
   * though it is disabled. A {@code GLJPanel} supports this better.
   */
  protected GLAutoDrawable createGLComponent(GLCapabilitiesImmutable capabilities, GLContext shareWith) {
    GLCanvas canvas = new GLCanvas(capabilities, shareWith);
    canvas.setEnabled(false);
    chosenCapabilities = (GLCapabilitiesImmutable) capabilities.cloneMutable();
    return canvas;
  }

  /**
   * Creates the GLEventListener that will draw the given component to the
   * canvas.
   */
  protected GLEventListener createG2DListener(JComponent drawingComponent) {
    return new GLG2DSimpleEventListener(drawingComponent);
  }

  /**
   * Calling {@link GLCanvas#removeNotify()} destroys the GLContext. We could
   * mess with that internally, but this is slightly easier.
   * <p>
   * This method is particularly important for docking frameworks and moving the
   * panel from one window to another. This is simple for normal Swing
   * components, but GL contexts are destroyed when {@code removeNotify()} is
   * called.
   * </p>
   * <p>
   * Our workaround is to use context sharing. The pbuffer is initialized and by
   * drawing into it at least once, we automatically share all textures, etc.
   * with the new pbuffer. This pbuffer holds the data until we can initialize
   * our new JOGL canvas. We share the pbuffer canvas with the new JOGL canvas
   * and everything works nicely from then on.
   * </p>
   * <p>
   * This has the unfortunate side-effect of leaking memory. I'm not sure how to
   * fix this yet.
   * </p>
   */
  @Override
  public void removeNotify() {
    prepareSideContext();

    remove((Component) canvas);
    super.removeNotify();

    canvas = createGLComponent(chosenCapabilities, sideContext.getContext());
    canvas.addGLEventListener(g2dglListener);
    add((Component) canvas, 0);
  }

  private void prepareSideContext() {
    if (sideContext == null) {
      GLDrawableFactory factory = canvas.getFactory();
      sideContext = factory.createOffscreenAutoDrawable(null, chosenCapabilities, null, 1, 1, canvas.getContext());
      sideContext.addGLEventListener(g2dglListener);
    }

    Runnable work = new Runnable() {
      @Override
      public void run() {
        sideContext.display();
      }
    };

    if (Threading.isOpenGLThread()) {
      work.run();
    } else {
      Threading.invokeOnOpenGLThread(false, work);
    }
  }

  @Override
  public void paint(Graphics g) {
    if (isGLDrawing() && drawableComponent != null && canvas != null) {
      canvas.display();
    } else {
      super.paint(g);
    }
  }

  @Override
  protected void paintChildren(Graphics g) {
    /*
     * Don't paint the drawableComponent. If we'd use a GLJPanel instead of a
     * GLCanvas, we'd have to paint it here.
     */
    if (!isGLDrawing()) {
      super.paintChildren(g);
    }
  }

  @Override
  protected void addImpl(Component comp, Object constraints, int index) {
    if (comp == canvas || comp == drawableComponent) {
      super.addImpl(comp, constraints, index);
    } else {
      throw new IllegalArgumentException("Do not add component to this. Add them to the object in getDrawableComponent()");
    }
  }

  /**
   * Implements a simple layout where all the components are the same size as
   * the parent.
   */
  protected static class GLOverlayLayout implements LayoutManager2, Serializable {
    private static final long serialVersionUID = -8248213786715565045L;

    @Override
    public Dimension preferredLayoutSize(Container parent) {
      if (parent.isPreferredSizeSet() || parent.getComponentCount() == 0) {
        return parent.getPreferredSize();
      } else {
        int x = -1, y = -1;
        for (Component child : parent.getComponents()) {
          Dimension dim = child.getPreferredSize();
          x = Math.max(dim.width, x);
          y = Math.max(dim.height, y);
        }

        return new Dimension(x, y);
      }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
      if (parent.getComponentCount() == 0) {
        return new Dimension(0, 0);
      } else {
        int x = Integer.MAX_VALUE, y = Integer.MAX_VALUE;
        for (Component child : parent.getComponents()) {
          Dimension dim = child.getMinimumSize();
          x = Math.min(dim.width, x);
          y = Math.min(dim.height, y);
        }

        return new Dimension(x, y);
      }
    }

    @Override
    public Dimension maximumLayoutSize(Container parent) {
      if (parent.getComponentCount() == 0) {
        return new Dimension(0, 0);
      } else {
        int x = -1, y = -1;
        for (Component child : parent.getComponents()) {
          Dimension dim = child.getMaximumSize();
          x = Math.max(dim.width, x);
          y = Math.max(dim.height, y);
        }

        return new Dimension(x, y);
      }
    }

    @Override
    public void layoutContainer(Container parent) {
      for (Component child : parent.getComponents()) {
        child.setSize(parent.getSize());
      }
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
      // nop
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
      // nop
    }

    @Override
    public void removeLayoutComponent(Component comp) {
      // nop
    }

    @Override
    public void invalidateLayout(Container target) {
      // nop
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
      return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
      return 0.5f;
    }
  }
}
