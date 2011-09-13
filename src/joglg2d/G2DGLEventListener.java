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

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

/**
 * @author borkholder
 * @created Jul 11, 2010
 */
public abstract class G2DGLEventListener implements GLEventListener {
  protected GLGraphics2D g2d;

  protected Component baseComponent;

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

  protected abstract void paintGL(GLGraphics2D g2d);

  @Override
  public void init(GLAutoDrawable drawable) {
    reshape(drawable, 0, 0, drawable.getWidth(), drawable.getWidth());
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL gl = drawable.getGL();
    if (height <= 0) {
      height = 1;
    }

    g2d = createGraphics2D(drawable);

    gl.glViewport(0, 0, width, height);
    gl.glMatrixMode(GL.GL_PROJECTION);
    gl.glLoadIdentity();
    gl.glOrtho(0, width, 0, height, -1, 1);
  }

  @Override
  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    // reinitialize
    g2d = createGraphics2D(drawable);
  }

  protected GLGraphics2D createGraphics2D(GLAutoDrawable drawable) {
    return new GLGraphics2D(drawable.getWidth(), drawable.getHeight());
  }
}
