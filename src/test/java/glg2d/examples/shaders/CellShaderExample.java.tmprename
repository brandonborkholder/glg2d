package glg2d.examples.shaders;

import glg2d.G2DGLEventListener;
import glg2d.G2DGLPanel;
import glg2d.GLGraphics2D;
import glg2d.UIDemo;
import glg2d.impl.gl2.GL2StringDrawer;
import glg2d.impl.gl2.GL2Transformhelper;
import glg2d.impl.gl2.GL2ColorHelper;
import glg2d.impl.shader.G2DShaderImageDrawer;
import glg2d.impl.shader.G2DShaderShapeDrawer;
import glg2d.impl.shader.GLShaderGraphics2D;
import glg2d.impl.shader.ResourceShader;
import glg2d.impl.shader.Shader;

import java.awt.Dimension;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class CellShaderExample {
  public static void main(String[] args) throws Exception {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    JFrame frame = new JFrame("Cell Shader Example");
    frame.setContentPane(new G2DGLPanel(new UIDemo()) {
      @Override
      protected G2DGLEventListener createG2DListener(JComponent drawingComponent) {
        return new G2DGLEventListener(drawingComponent) {
          @Override
          protected GLGraphics2D createGraphics2D(GLAutoDrawable drawable) {
            return new GLShaderGraphics2D() {
              @Override
              protected void createDrawingHelpers() {
                Shader s = new ResourceShader(CellShaderExample.class, "CellShader.v", "CellShader.f");
                shapeHelper = new G2DShaderShapeDrawer(s);

                s = new ResourceShader(CellShaderExample.class, "CellShader.v", "CellTextureShader.f");
                imageHelper = new G2DShaderImageDrawer(s);
                stringHelper = new GL2StringDrawer();

                colorHelper = new GL2ColorHelper();
                matrixHelper = new GL2Transformhelper();

                addG2DDrawingHelper(shapeHelper);
                addG2DDrawingHelper(imageHelper);
                addG2DDrawingHelper(stringHelper);
                addG2DDrawingHelper(colorHelper);
                addG2DDrawingHelper(matrixHelper);
              }
            };
          }
        };
      }
    });

    // frame.setContentPane(new UIDemo());
    frame.setPreferredSize(new Dimension(1024, 768));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
