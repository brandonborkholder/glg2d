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

import java.awt.Component;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

/**
 * Helps wrap the {@code GLGraphics2D} object within the JOGL framework.
 */
public abstract class G2DGLEventListener implements GLEventListener {
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
    g2d.setCanvas(drawable);
    g2d.prePaint(baseComponent);
    paintGL(g2d);
    g2d.postPaint();
  }

  /**
   * Paints using the {@code GLGraphics2D} object. This could be forwarded to
   * any code that expects to draw using the Java2D framework.
   */
  protected abstract void paintGL(GLGraphics2D g2d);

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
