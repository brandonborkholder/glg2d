package joglg2d;

import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JPanel;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;

/**
 * @author borkholder
 * @created Feb 6, 2010
 */
@SuppressWarnings("serial")
public class JOGLPanel extends JPanel {
  protected GLCanvas contentPanel;

  protected JOGLG2D g2d;

  protected Animator animator;

  public JOGLPanel() {
    GLCapabilities capabilities = new GLCapabilities();
    contentPanel = new GLCanvas(capabilities);
    contentPanel.addGLEventListener(new Listener());

    setLayout(new BorderLayout());
    add(contentPanel, BorderLayout.CENTER);

    animator = new FPSAnimator(contentPanel, 5);
    animator.start();
  }

  @Override
  public void repaint() {
  }

  @Override
  public Graphics getGraphics() {
    return g2d;
  }

  class Listener implements GLEventListener {
    @Override
    public void display(GLAutoDrawable drawable) {
      g2d.paint(JOGLPanel.this);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
//      contentPanel.setGL(new TraceGL(contentPanel.getGL(), System.out));
      g2d = new JOGLG2D(contentPanel.getGL(), drawable.getHeight());
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
      GL gl = drawable.getGL();
      if (height <= 0) {
        height = 1;
      }

      gl.glViewport(0, 0, width, height);
      gl.glMatrixMode(GL.GL_PROJECTION);
      gl.glLoadIdentity();
      gl.glMatrixMode(GL.GL_MODELVIEW);
      gl.glLoadIdentity();
      new GLU().gluOrtho2D(0, width, 0, height);

      g2d = new JOGLG2D(gl, height);
    }

    @Override
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
  }
}
