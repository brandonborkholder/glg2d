package joglg2d;

import java.awt.Graphics2D;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;

/**
 * @author borkholder
 * @created Feb 6, 2010
 */
@SuppressWarnings("serial")
public class JOGLPanel extends GLCanvas {
  protected JOGLG2D g2d;

  protected Animator animator;

  public JOGLPanel() {
    addGLEventListener(new Listener());

    animator = new FPSAnimator(this, 5);
    animator.start();
  }

  public void paintGL(Graphics2D g2d) {
  }

  class Listener implements GLEventListener {
    @Override
    public void display(GLAutoDrawable drawable) {
      g2d.prePaint(JOGLPanel.this);
      paintGL(g2d);
      g2d.postPaint();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
      // contentPanel.setGL(new TraceGL(contentPanel.getGL(), System.out));
      g2d = new JOGLG2D(drawable.getGL(), drawable.getWidth(), drawable.getHeight());
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

      g2d = new JOGLG2D(gl, width, height);
    }

    @Override
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
  }
}
