package glg2d.examples.shaders;

import glg2d.G2DDrawingHelper;
import glg2d.G2DGLEventListener;
import glg2d.G2DGLPanel;
import glg2d.GLGraphics2D;
import glg2d.UIDemo;

import java.awt.Dimension;
import java.awt.RenderingHints.Key;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.GL2;
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
    frame.setContentPane(new G2DGLPanel(new UIDemo()) {
      @Override
      protected G2DGLEventListener createG2DListener(JComponent drawingComponent) {
        return new G2DGLEventListener(drawingComponent) {
          @Override
          protected GLGraphics2D createGraphics2D(GLAutoDrawable drawable) {
            return new GLGraphics2D() {
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

    Timer timer = new Timer(100, new ActionListener() {
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

    GL2 gl;

    @Override
    public void dispose() {
    }

    @Override
    public void pop(GLGraphics2D parentG2d) {
      gl.glTranslated(-shiftX, -shiftY, 0);
    }

    @Override
    public void push(GLGraphics2D newG2d) {
      gl.glTranslated(shiftX, shiftY, 0);
    }

    @Override
    public void resetHints() {
    }

    @Override
    public void setHint(Key key, Object value) {
    }

    @Override
    public void setG2D(GLGraphics2D g2d) {
      theta += 0.2;
      shiftX = Math.round(Math.sin(theta) * 100) / 100d * 1;
      shiftY = Math.round(Math.cos(theta) * 100) / 100d * 1;

      gl = g2d.getGLContext().getGL().getGL2();
    }
  }
}