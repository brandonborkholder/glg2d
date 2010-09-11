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

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLJPanel;

/**
 * @author borkholder
 * @created Feb 6, 2010
 */
@SuppressWarnings("serial")
public class JOGLPanel extends GLJPanel {
  public static GLCapabilities getDefaultCapabalities() {
    GLCapabilities caps = new GLCapabilities();
    caps.setRedBits(8);
    caps.setGreenBits(8);
    caps.setBlueBits(8);
    caps.setAlphaBits(8);
    caps.setDoubleBuffered(true);
    caps.setHardwareAccelerated(true);
    return caps;
  }
    
  public JOGLPanel() {
    this(getDefaultCapabalities());
  }
  
  public JOGLPanel(GLCapabilities caps) {
    addGLEventListener(new Graphics2DListener(this) {
      @Override
      protected void paintGL(JOGLG2D g2d) {
        JOGLPanel.this.paintGL(g2d);
      }
    });
  }

  protected void paintGL(JOGLG2D g2d) {
  }
}
