package joglg2d;

import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;

/**
 * @author borkholder
 * @created Feb 6, 2010
 */
@SuppressWarnings("serial")
public class JOGLPanel extends JPanel {
  protected GLCanvas contentPanel;

  protected JOGLG2D g2d;

  public JOGLPanel() {
    GLCapabilities capabilities = new GLCapabilities(GLProfile.getDefault());
    capabilities.setDoubleBuffered(true);
    capabilities.setHardwareAccelerated(true);

    contentPanel = new GLCanvas(capabilities);
    contentPanel.addGLEventListener(new Listener());
    g2d = new JOGLG2D(contentPanel.getGL());

    setLayout(new BorderLayout());
    add(contentPanel, BorderLayout.CENTER);
  }

  @Override
  public Graphics getGraphics() {
    return g2d;
  }

  class Listener implements GLEventListener {
    @Override
    public void display(GLAutoDrawable drawable) {
      paint(getGraphics());
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
      // TODO Auto-generated method stub
    }

    @Override
    public void init(GLAutoDrawable drawable) {
      // TODO Auto-generated method stub
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
      // TODO Auto-generated method stub
    }
  }
}
