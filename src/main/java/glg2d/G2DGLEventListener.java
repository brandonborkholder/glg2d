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

import java.awt.Component;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.swing.RepaintManager;

/**
 * Helps wrap the {@code GLGraphics2D} object within the JOGL framework.
 */
public class G2DGLEventListener implements GLEventListener {
  protected GLGraphics2D g2d;

  protected Component baseComponent;

  /**
   * Creates a new listener that will paint using the {@code GLGraphics2D}
   * object on each call to {@link #display(GLAutoDrawable)}. The provided
   * {@code baseComponent} is used to provide default font, backgroundColor,
   * etc. to the {@code GLGraphics2D} object. It is also used for width, height
   * of the viewport in OpenGL.
   * 
   * @param baseComponent
   *          The component to use for default settings.
   */
  public G2DGLEventListener(Component baseComponent) {
    this.baseComponent = baseComponent;
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    prePaint(drawable);
    paintGL(g2d);
    postPaint(drawable);
  }

  /**
   * Called after the canvas is set on {@code g2d} but before any painting is
   * done. This should setup the matrices and ask {@code g2d} to setup any
   * client state.
   */
  protected void prePaint(GLAutoDrawable drawable) {
    setupMatrices(drawable.getContext());
    g2d.prePaint(drawable, baseComponent);
  }

  /**
   * Sets up the three matrices, including the transform from OpenGL coordinate
   * system to Java2D coordinates.
   */
  protected void setupMatrices(GLContext context) {
    GL gl = context.getGL();

    // push all the matrices
    gl.glMatrixMode(GL.GL_PROJECTION);
    gl.glPushMatrix();

    int width = baseComponent.getWidth();
    int height = baseComponent.getHeight();

    // and setup the viewport
    gl.glViewport(0, 0, width, height);
    gl.glLoadIdentity();
    gl.glOrtho(0, width, 0, height, -1, 1);

    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glPushMatrix();
    gl.glLoadIdentity();

    // do the transform from Graphics2D coords to openGL coords
    gl.glTranslatef(0, height, 0);
    gl.glScalef(1, -1, 1);

    gl.glMatrixMode(GL.GL_TEXTURE);
    gl.glPushMatrix();
    gl.glLoadIdentity();
  }

  /**
   * Called after all Java2D painting is complete. This should restore the
   * matrices if they were modified.
   */
  protected void postPaint(GLAutoDrawable drawable) {
    g2d.postPaint();
    popMatrices(drawable.getContext());
  }

  /**
   * Pops all the matrices.
   */
  protected void popMatrices(GLContext context) {
    GL gl = context.getGL();
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glPopMatrix();
    gl.glMatrixMode(GL.GL_PROJECTION);
    gl.glPopMatrix();
    gl.glMatrixMode(GL.GL_TEXTURE);
    gl.glPopMatrix();
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
    RepaintManager mgr = RepaintManager.currentManager(baseComponent);
    boolean doubleBuffer = mgr.isDoubleBufferingEnabled();
    mgr.setDoubleBufferingEnabled(false);
    baseComponent.paint(g2d);
    mgr.setDoubleBufferingEnabled(doubleBuffer);
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    reshape(drawable, 0, 0, drawable.getWidth(), drawable.getWidth());
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    if (height <= 0) {
      height = 1;
    }

    g2d = createGraphics2D(drawable);
  }

  @Override
  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    reshape(drawable, 0, 0, drawable.getWidth(), drawable.getWidth());
  }

  protected GLGraphics2D createGraphics2D(GLAutoDrawable drawable) {
    return new GLGraphics2D(drawable.getWidth(), drawable.getHeight());
  }
}
