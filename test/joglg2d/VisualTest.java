package joglg2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

import javax.swing.ImageIcon;

import joglg2d.util.Painter;
import joglg2d.util.TestWindow;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author borkholder
 * @created Feb 6, 2010
 */
public class VisualTest {
  static TestWindow tester;

  @BeforeClass
  public static void initialize() {
    tester = new TestWindow();
  }

  @AfterClass
  public static void close() {
    tester.close();
  }

  @Test
  public void lineTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.drawLine(10, 10, 50, 50);
        g2d.drawLine(5, 60, 50, 60);
        g2d.drawLine(70, 90, 70, 140);
      }
    });

    tester.assertSame();
  }

  @Test
  public void fillRectTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setColor(Color.BLUE);
        g2d.fillRect(50, 123, 99, 7);
      }
    });

    tester.assertSame();
  }

  @Test
  public void lineWidthTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(6));
        g2d.drawLine(15, 99, 143, 400);
      }
    });

    tester.assertSame();
  }

  @Test
  public void drawRectTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.drawRect(50, 90, 70, 32);
      }
    });

    tester.assertSame();
  }

  @Test
  public void rectangleShapeTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.draw(new Rectangle2D.Float(48, 123, 49, 34));
      }
    });

    tester.assertSame();
  }

  @Test
  public void strokedShapeTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        g2d.draw(new Rectangle2D.Float(48, 123, 49, 34));
      }
    });

    tester.assertSame();
  }

  @Test
  public void roundRectShapeTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(5));
        g2d.draw(new RoundRectangle2D.Float(99, 40, 230, 493, 90, 70));
      }
    });

    tester.assertSame();
  }

  @Test
  public void drawPolylineTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
        g2d.drawPolyline(new int[] { 8, 43, 94, 16 }, new int[] { 43, 99, 34, 75 }, 4);
      }
    });

    tester.assertSame();
  }

  @Test
  public void drawPolygonTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.drawPolygon(new int[] { 8, 23, 98, 42 }, new int[] { 47, 23, 43, 25 }, 4);
      }
    });

    tester.assertSame();
  }

  @Test
  public void fillPolygonTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setColor(Color.CYAN);
        g2d.fillPolygon(new int[] { 8, 23, 98, 42 }, new int[] { 47, 23, 43, 25 }, 4);
      }
    });

    tester.assertSame();
  }

  @Test
  public void drawStringTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.drawString("Hello JOGL", 90, 32);
      }
    });

    tester.assertSame();
  }

  @Test
  public void drawImageTest() throws Exception {
    URL url = VisualTest.class.getClassLoader().getResource("duke.gif");
    final ImageIcon icon = new ImageIcon(url);
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.drawImage(icon.getImage(), AffineTransform.getTranslateInstance(50, 90), null);
      }
    });

    tester.assertSame();
  }

  @Test
  public void clipTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setClip(new Rectangle(10, 50, 70, 90));
        g2d.setColor(Color.BLUE);
        g2d.fill(new Rectangle(40, 60, 60, 150));
      }
    });

    tester.assertSame();
  }

  @Test
  public void capTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        g2d.drawLine(19, 239, 98, 42);
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(14, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        g2d.drawLine(19, 39, 98, 342);

        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
        g2d.drawLine(149, 274, 118, 142);
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(14, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
        g2d.drawLine(219, 139, 98, 242);
      }
    });

    tester.assertSame();
  }

  @Test
  public void joinTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(7, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        g2d.draw(new Rectangle2D.Double(50, 100, 120, 90));

        Path2D.Double path = new Path2D.Double();
        path.moveTo(180, 50);
        path.lineTo(210, 30);
        path.lineTo(280, 70);
        path.lineTo(210, 38);
        path.closePath();
        g2d.draw(path);

        path = new Path2D.Double();
        path.moveTo(180, 250);
        path.lineTo(180, 230);
        path.lineTo(280, 209);
        path.lineTo(240, 283);
        path.closePath();
        g2d.draw(path);

        Arc2D.Double arc = new Arc2D.Double(77, 349, 60, 40, 0, 70, Arc2D.OPEN);
        g2d.draw(arc);
      }
    });

    tester.assertSame();
  }
}
