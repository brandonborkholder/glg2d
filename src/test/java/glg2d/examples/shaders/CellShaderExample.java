package glg2d.examples.shaders;

import glg2d.G2DGLCanvas;
import glg2d.G2DGLEventListener;

import java.awt.Dimension;

import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class CellShaderExample {
  public static void main(String[] args) throws Exception {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    JFrame frame = new JFrame("Cell Shader Example");
    frame.setContentPane(new G2DGLCanvas(new UIDemo()) {
      @Override
      protected GLEventListener createG2DListener(JComponent drawingComponent) {
        return new G2DGLEventListener(drawingComponent) {
          CellShaderHelper helper;

          @Override
          protected void prePaint(GLContext context) {
            super.prePaint(context);
            helper = new CellShaderHelper(context);
            helper.setupShader();
          }

          @Override
          protected void postPaint(GLContext context) {
            super.postPaint(context);
            helper.endShader();
          }
        };
      }
    });
    
    
//     frame.setContentPane(new UIDemo());
    frame.setPreferredSize(new Dimension(1024, 768));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
