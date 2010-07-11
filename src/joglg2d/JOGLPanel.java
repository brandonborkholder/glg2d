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

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.swing.JPanel;

/**
 * @author borkholder
 * @created Feb 6, 2010
 */
@SuppressWarnings("serial")
public class JOGLPanel extends JPanel {
  protected GLAutoDrawable drawable;

  public JOGLPanel() {
    GLCanvas canvas = new GLCanvas();
    canvas.addGLEventListener(new Graphics2DListener(canvas) {
      @Override
      protected void paintGL(JOGLG2D g2d) {
        JOGLPanel.this.paintGL(g2d);
      }
    });

    setLayout(new BorderLayout());
    add(canvas, BorderLayout.CENTER);

    this.drawable = canvas;
  }

  @Override
  public void paint(Graphics g) {
    drawable.display();
    super.paint(g);
  }

  protected void paintGL(JOGLG2D g2d) {
  }
}
