package glg2d.examples.shaders;

import glg2d.G2DDrawingHelper;
import glg2d.G2DGLCanvas;
import glg2d.G2DGLEventListener;
import glg2d.GLGraphics2D;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class DepthSimExample {
  public static void main(String[] args) throws Exception {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    final JFrame frame = new JFrame("Depth Shaker Example");
    frame.setContentPane(new G2DGLCanvas(new UIDemo()) {
      @Override
      protected G2DGLEventListener createG2DListener(JComponent drawingComponent) {
        return new G2DGLEventListener(drawingComponent) {
          @Override
          protected GLGraphics2D createGraphics2D(GLAutoDrawable drawable) {
            return new GLGraphics2D(drawable.getWidth(), drawable.getHeight()) {
              @Override
              protected void createDrawingHelpers() {
                super.createDrawingHelpers();

                addG2DDrawingHelper(new DepthShaker());
              }

              @Override
              protected void scissor(boolean enable) {
                // nop
              }
            };
          }
        };
      }
    });

    Timer timer = new Timer(50, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        frame.getContentPane().repaint();
      }
    });
    timer.setRepeats(true);
    timer.start();

    // frame.setContentPane(new UIDemo());
    frame.setPreferredSize(new Dimension(1024, 768));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  static class DepthShaker implements G2DDrawingHelper {
    double shiftX;
    double shiftY;

    double theta = 0;

    GL gl;

    @Override
    public void dispose() {
    }

    @Override
    public void pop(GLGraphics2D parentG2d) {
      gl.glMatrixMode(GL.GL_MODELVIEW);
      gl.glPopMatrix();
    }

    @Override
    public void push(GLGraphics2D newG2d) {
      gl.glMatrixMode(GL.GL_MODELVIEW);
      gl.glPushMatrix();
      gl.glTranslated(shiftX, shiftY, 0);
    }

    @Override
    public void setG2D(GLGraphics2D g2d) {
      theta += 0.2;
      shiftX = Math.round(Math.sin(theta) * 100) / 100d * 2;
      shiftY = Math.round(Math.cos(theta) * 100) / 100d * 2;

      gl = g2d.getGLContext().getGL();
    }
  }
}