import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;

public class SimpleGL {
  public static void main(String[] args) {
    JFrame frame = new JFrame("OpenGL Test");

    GLCanvas canvas = new GLCanvas();
    canvas.addGLEventListener(new Listener());

    frame.setLayout(new BorderLayout());
    frame.add(canvas, BorderLayout.CENTER);
    frame.setPreferredSize(new Dimension(640, 480));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private static class Listener implements GLEventListener {
    @Override
    public void display(GLAutoDrawable drawable) {
      GL gl = drawable.getGL();

      gl.glClearColor(1, 1, 1, 1);
      gl.glClear(GL.GL_COLOR_BUFFER_BIT);

      gl.glEnable(GL.GL_BLEND);
      gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
      gl.glColor4f(1, 0, 0, 0.1f);
      gl.glRectf(50, 50, 200, 300);
    }

    @Override
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    @Override
    public void init(GLAutoDrawable drawable) {
      reshape(drawable, 0, 0, drawable.getWidth(), drawable.getHeight());
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
      gl.glOrtho(0, width, 0, height, -1, 1);
    }
  }
}
