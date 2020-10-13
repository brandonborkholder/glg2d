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
package net.opengrabeso.glg2d;

import com.github.opengrabeso.jaagl.GL;
import com.github.opengrabeso.jaagl.jogl.JoGL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import net.opengrabeso.glg2d.impl.shader.GLShaderGraphics2D;

import javax.swing.JComponent;

/**
 * Wraps a {@code JComponent} and paints it using a {@code GLGraphics2D}. This
 * object will paint the entire component fully for each frame.
 *
 * <p>
 * {@link GLG2DHeadlessListener} may also be used to listen for reshapes and
 * update the size and layout of the painted Swing component.
 * </p>
 */
public class GLG2DSimpleEventListener implements GLEventListener {
  /**
   * The cached object.
   */
  protected GLGraphics2D g2d;

  /**
   * The component to paint.
   */
  protected JComponent comp;

  public GLG2DSimpleEventListener(JComponent component) {
    if (component == null) {
      throw new NullPointerException("component is null");
    }

    this.comp = component;
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    prePaint(drawable);
    paintGL(g2d);
    postPaint(drawable);
  }

  /**
   * Called before any painting is done. This should setup the matrices and ask
   * the {@code GLGraphics2D} object to setup any client state.
   */
  protected void prePaint(GLAutoDrawable drawable) {
    setupViewport(g2d.getGL(), drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
    g2d.prePaint(g2d.getGL());

    // clip to only the component we're painting
    g2d.translate(comp.getX(), comp.getY());
    g2d.clipRect(0, 0, comp.getWidth(), comp.getHeight());
  }

  /**
   * Defines the viewport to paint into.
   */
  protected void setupViewport(GL gl, int width, int height) {
    gl.glViewport(0, 0, width, height);
  }

  /**
   * Called after all Java2D painting is complete.
   */
  protected void postPaint(GLAutoDrawable drawable) {
    g2d.postPaint();
  }

  /**
   * Paints using the {@code GLGraphics2D} object. This could be forwarded to
   * any code that expects to draw using the Java2D framework.
   * <p>
   * Currently is paints the component provided, turning off double-buffering in
   * the {@code RepaintManager} to force drawing directly to the
   * {@code Graphics2D} object.
   * </p>
   */
  protected void paintGL(GLGraphics2D g2d) {
    boolean wasDoubleBuffered = comp.isDoubleBuffered();
    comp.setDoubleBuffered(false);

    comp.paint(g2d);

    comp.setDoubleBuffered(wasDoubleBuffered);
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    g2d = createGraphics2D(drawable);
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
  }

  /**
   * Creates the {@code Graphics2D} object that forwards Java2D calls to OpenGL
   * calls.
   */
  protected GLGraphics2D createGraphics2D(GLAutoDrawable drawable) {
    return new GLShaderGraphics2D(JoGL.wrap(drawable.getContext().getGL().getGL3()));
    //return new GLGraphics2D(JoGL.wrap(drawable.getContext().getGL().getGL2()));
  }

  @Override
  public void dispose(GLAutoDrawable arg0) {
    if (g2d != null) {
      g2d.glDispose();
      g2d = null;
    }
  }
}
