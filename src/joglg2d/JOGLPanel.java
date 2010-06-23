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

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.JPanel;

/**
 * @author borkholder
 * @created Feb 6, 2010
 */
@SuppressWarnings("serial")
public class JOGLPanel extends JPanel {
  protected JOGLG2D g2d;

  protected GLCanvas canvas;

  public JOGLPanel() {
    canvas = new GLCanvas();
    canvas.addGLEventListener(new Listener());

    setLayout(new BorderLayout());
    add(canvas, BorderLayout.CENTER);
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    canvas.display();
  }

  public void paintGL(Graphics2D g2d) {
  }

  class Listener implements GLEventListener {
    @Override
    public void display(GLAutoDrawable drawable) {
      g2d.prePaint(canvas);
      paintGL(g2d);
      g2d.postPaint();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
      g2d = new JOGLG2D(drawable.getGL(), drawable.getWidth(), drawable.getHeight());
      drawable.getGL().glEnable(GL.GL_DOUBLEBUFFER);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
      GL gl = drawable.getGL();
      if (height <= 0) {
        height = 1;
      }

      g2d = new JOGLG2D(gl, width, height);
    }

    @Override
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
  }
}
