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

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.swing.JComponent;
import javax.swing.RepaintManager;

/**
 * Helps wrap the {@code GLGraphics2D} object within the JOGL framework and
 * paints the component fully for each display.
 */
public class G2DGLSimpleEventListener implements GLEventListener {
  protected GLGraphics2D g2d;

  protected JComponent baseComponent;

  /**
   * Creates a new listener that will paint using the {@code baseComponent} on
   * each call to {@link #display(GLAutoDrawable)}. The provided
   * {@code baseComponent} is used to provide default font, backgroundColor,
   * etc. to the {@code GLGraphics2D} object. It is also used for width, height
   * of the viewport in OpenGL.
   */
  public G2DGLSimpleEventListener(JComponent baseComponent) {
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
    g2d.prePaint(drawable, baseComponent);
  }

  /**
   * Called after all Java2D painting is complete. This should restore the
   * matrices if they were modified.
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

    dispose(drawable);

    g2d = createGraphics2D(drawable);
  }

  /**
   * Creates the {@code Graphics2D} object that forwards Java2D calls to OpenGL
   * calls.
   */
  protected GLGraphics2D createGraphics2D(GLAutoDrawable drawable) {
    return new GLGraphics2D();
  }

  @Override
  public void dispose(GLAutoDrawable arg0) {
    if (g2d != null) {
      g2d.glDispose();
      g2d = null;
    }
  }
}
