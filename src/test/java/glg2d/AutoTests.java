package glg2d;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;

import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLPbuffer;
import javax.swing.JPanel;

import org.junit.Assert;
import org.junit.Test;

public class AutoTests {
  @Test
  public void testTransform() {
    GLPbuffer buffer = GLDrawableFactory.getFactory().createGLPbuffer(G2DGLCanvas.getDefaultCapabalities(), null, 500, 500, null);
    JPanel painter = new JPanel() {
      public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g.create();
        AffineTransform xform = new AffineTransform();
        g2d.translate(50, 50);
        xform.translate(50, 50);
        g2d.scale(3, -1);
        xform.scale(3, -1);
        g2d.shear(2, .05);
        xform.shear(2, .05);
        g2d.translate(-50, 50);
        xform.translate(-50, 50);
        
        AffineTransform glXform = g2d.getTransform();
        Point pt = new Point(0, 0);
        Point pt2 = new Point();
        Point pt3 = new Point();
        xform.transform(pt, pt2);
        glXform.transform(pt, pt3);
        
        Assert.assertEquals(pt2.x, pt3.x);
        Assert.assertEquals(pt2.y, pt3.y);
        
        g2d.dispose();
      }
    };
    
    buffer.addGLEventListener(new G2DGLEventListener(painter));
    buffer.display();
  }
}
