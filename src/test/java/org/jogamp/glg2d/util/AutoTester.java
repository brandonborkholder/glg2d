package org.jogamp.glg2d.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jogamp.glg2d.GLG2DCanvas;
import org.jogamp.glg2d.GLG2DSimpleEventListener;
import org.jogamp.glg2d.GLGraphics2D;
import org.junit.Assert;

import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;

public class AutoTester implements Tester {
  static final int pixels = 500;


  static GLOffscreenAutoDrawable buffer = GLDrawableFactory.getFactory(GLProfile.getMaxFixedFunc(true))
          .createOffscreenAutoDrawable(null, GLG2DCanvas.getDefaultCapabalities(), null, pixels, pixels);

  private Painter p;

  @Override
  public void assertSame() throws InterruptedException {
    Assert.assertEquals(1, getSimilarityScore(p), 0.05);
  }

  @Override
  public void setPainter(Painter p) {
    this.p = p;
  }

  @Override
  public void finish() {
  }

  public double getSimilarityScore(Painter painter) {
    BufferedImage gl = drawGL(painter);
    Raster rasterGl = gl.getData();
    BufferedImage g2d = drawG2D(painter);
    Raster rasterG2d = g2d.getData();

    // very naive for now
    for (int band = 0; band < rasterGl.getNumBands(); band++) {
      for (int row = 0; row < gl.getWidth(); row++) {
        for (int col = 0; col < gl.getHeight(); col++) {
          if (rasterGl.getSample(row, col, band) != rasterG2d.getSample(row, col, band)) {
            return 0;
          }
        }
      }
    }

    return 1;
  }

  public JSplitPane getComparison(Painter painter) {
    JSplitPane split = new JSplitPane();
    split.setLeftComponent(new JLabel(new ImageIcon(drawGL(painter))));
    split.setRightComponent(new JLabel(new ImageIcon(drawG2D(painter))));
    return split;
  }

  public static BufferedImage drawG2D(Painter painter) {
    BufferedImage img = new BufferedImage(pixels, pixels, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = (Graphics2D) img.getGraphics();
    g2d.setColor(new JPanel().getBackground());
    g2d.fillRect(0, 0, pixels, pixels);
    painter.paint(g2d);
    return img;
  }

  public static BufferedImage drawGL(final Painter painter) {
    JPanel panel = new JPanel();
    panel.setSize(pixels, pixels);
    buffer.addGLEventListener(new GLG2DSimpleEventListener(panel) {
      @Override
      protected void paintGL(GLGraphics2D g2d) {
        painter.paint(g2d);
      }
    });

    buffer.display();
    buffer.getContext().makeCurrent();
    AWTGLReadBufferUtil util = new AWTGLReadBufferUtil(GLG2DCanvas.getDefaultCapabalities().getGLProfile(), false);
    return util.readPixelsToBufferedImage( buffer.getContext().getGL( ), true );
  }
}
