package glg2d.examples.shaders;

import glg2d.G2DGLColorHelper;
import glg2d.G2DGLEventListener;
import glg2d.G2DGLPanel;
import glg2d.G2DGLTransformHelper;
import glg2d.GLGraphics2D;
import glg2d.UIDemo;
import glg2d.shaders.G2DShaderImageDrawer;
import glg2d.shaders.G2DShaderShapeDrawer;
import glg2d.shaders.G2DShaderStringDrawer;
import glg2d.shaders.GLShaderGraphics2D;
import glg2d.shaders.ResourceShader;
import glg2d.shaders.Shader;

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
                shapeDrawer = new G2DShaderShapeDrawer(s);

                s = new ResourceShader(CellShaderExample.class, "CellShader.v", "CellTextureShader.f");
                imageDrawer = new G2DShaderImageDrawer(s);
                stringDrawer = new G2DShaderStringDrawer(s);
                
                colorHelper = new G2DGLColorHelper();
                matrixHelper = new G2DGLTransformHelper();

                addG2DDrawingHelper(shapeDrawer);
                addG2DDrawingHelper(imageDrawer);
                addG2DDrawingHelper(stringDrawer);
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
