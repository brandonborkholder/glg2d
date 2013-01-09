package org.jogamp.glg2d;


import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.plaf.metal.MetalIconFactory;

import org.jogamp.glg2d.util.Painter;
import org.jogamp.glg2d.util.TestWindow;
import org.jogamp.glg2d.util.Tester;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class VisualTest {
  static Tester tester;

  @BeforeClass
  public static void initialize() {
    // XXX if doing visual inspection
    tester = new TestWindow();
    // if automated pixel comparison
    // tester = new AutoTester();
  }

  @AfterClass
  public static void close() {
    tester.finish();
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
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
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
        g2d.setColor(new Color(0, 0, 0, 0.4f));
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
  public void fillNonconvexPolygonTest() throws Exception {
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
  public void fillConvexPolygonTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.fillPolygon(new int[] { 5, 50, 60, 30 }, new int[] { 10, 15, 80, 90 }, 4);
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

        g2d.setColor(Color.red);
        g2d.drawString("foo", 120, 50);

        g2d.setFont(new Font("Serif", Font.PLAIN, 36));
        g2d.rotate(0.5);
        g2d.setColor(Color.blue);
        g2d.drawString("bar", 150, 90);
      }
    });

    tester.assertSame();
  }

  @Test
  public void drawImageTest() throws Exception {
    URL url = VisualTest.class.getClassLoader().getResource("duke.gif");
    final BufferedImage image = ImageIO.read(url);
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        AffineTransform xform = AffineTransform.getTranslateInstance(50, 90);
        xform.rotate(0.1);
        g2d.drawImage(image, xform, null);
      }
    });

    tester.assertSame();
  }

  @Test
  public void clipTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        Rectangle2D inf = new Rectangle2D.Double(-100, -100, 1000, 1000);
        g2d.setClip(new Rectangle(50, 50, 50, 50));
        g2d.setColor(Color.BLUE);
        g2d.fill(inf);

        Graphics2D g2 = (Graphics2D) g2d.create(10, 10, 10, 10);
        g2.setColor(Color.RED);
        g2.fill(inf);
        g2.dispose();

        g2 = (Graphics2D) g2d.create(60, 60, 20, 20);
        g2.setColor(Color.YELLOW);
        g2.fill(inf);

        g2.translate(30, 30);
        g2.setColor(Color.CYAN);
        g2.fill(inf);

        g2.setClip(10, 10, 90, 90);
        g2.setColor(Color.ORANGE);
        g2.fill(inf);
        g2.dispose();
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
        g2d.drawLine(219, 139, 88, 242);

        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        g2d.drawLine(49, 300, 110, 140);
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(14, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        g2d.drawLine(19, 300, 8, 242);
      }
    });

    tester.assertSame();
  }

  private void strokedShapes(Graphics2D g2d) {
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

    g2d.setColor(Color.red);
    path = new Path2D.Double();
    path.moveTo(240, 383);
    path.lineTo(280, 309);
    path.lineTo(180, 330);
    path.lineTo(180, 350);
    path.closePath();
    g2d.draw(path);
  }

  @Test
  public void miterJoinTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(7, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        strokedShapes(g2d);
      }
    });

    tester.assertSame();
  }

  @Test
  public void roundJoinTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(7, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
        strokedShapes(g2d);
      }
    });

    tester.assertSame();
  }

  @Test
  public void bevelJoinTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(7, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
        strokedShapes(g2d);
      }
    });

    tester.assertSame();
  }

  @Test
  public void createTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setColor(Color.red);
        g2d.fillRect(0, 0, 5, 5);
        g2d.translate(50, 9);
        g2d.setColor(Color.blue);
        g2d.fillRect(0, 0, 5, 5);

        Graphics2D g = (Graphics2D) g2d.create();
        g.setColor(Color.orange);
        g.fillRect(10, 10, 5, 5);
        g.translate(10, 50);
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 5, 5);

        g.clipRect(50, 50, 10, 10);
        g.setBackground(Color.black);
        g.clearRect(0, 0, 90, 90);
        g.dispose();

        g2d.setColor(Color.blue);
        g2d.fillRect(5, 5, 5, 5);
      }
    });

    tester.assertSame();
  }

  @Test
  public void labelTest() throws Exception {
    final JLabel label1 = new JLabel("foo");
    label1.setSize(label1.getPreferredSize());
    final JLabel label2 = new JLabel("bar");
    label2.setSize(label2.getPreferredSize());
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        label1.setBackground(Color.red);
        label1.setForeground(Color.white);
        label1.setOpaque(true);
        label2.setBackground(Color.blue);
        label2.setForeground(Color.pink);
        label2.setOpaque(true);

        g2d.setBackground(Color.yellow);
        g2d.clearRect(0, 0, 200, 400);

        g2d.translate(50, 70);
        label1.paint(g2d);
        g2d.translate(0, 143);
        label2.paint(g2d);
      }
    });

    tester.assertSame();
  }

  @Test
  public void curveTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        QuadCurve2D quad = new QuadCurve2D.Double(5, 5, 50, 20, 20, 25);
        g2d.draw(quad);

        g2d.scale(2, 2);
        Arc2D arc = new Arc2D.Double(10, 149, 40, 73, 43, 123, Arc2D.CHORD);
        g2d.draw(arc);
        g2d.scale(.5, .5);

        CubicCurve2D cubic = new CubicCurve2D.Double(249, 99, 212, 298, 140, 250, 10, 140);
        g2d.draw(cubic);

        Ellipse2D ellipse = new Ellipse2D.Double(70, 134, 49, 73);
        g2d.draw(ellipse);
      }
    });

    tester.assertSame();
  }

  @Test
  public void compositeTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setBackground(Color.white);
        g2d.clearRect(0, 0, 500, 500);

        draw(g2d, AlphaComposite.Src, "Src");
        g2d.translate(100, 0);
        draw(g2d, AlphaComposite.SrcIn, "SrcIn");
        g2d.translate(100, 0);
        draw(g2d, AlphaComposite.SrcOut, "SrcOut");
        g2d.translate(-200, 100);
        draw(g2d, AlphaComposite.SrcOver, "SrcOver");
        g2d.translate(100, 0);
        draw(g2d, AlphaComposite.SrcAtop, "SrcAtop");
        g2d.translate(100, 0);
        draw(g2d, AlphaComposite.Dst, "Dst");
        g2d.translate(-200, 100);
        draw(g2d, AlphaComposite.DstIn, "DstIn");
        g2d.translate(100, 0);
        draw(g2d, AlphaComposite.DstOut, "DstOut");
        g2d.translate(100, 0);
        draw(g2d, AlphaComposite.DstOver, "DstOver");
        g2d.translate(-200, 100);
        draw(g2d, AlphaComposite.DstAtop, "DstAtop");
        g2d.translate(100, 0);
        draw(g2d, AlphaComposite.Xor, "Xor");
        g2d.translate(100, 0);
        draw(g2d, AlphaComposite.Clear, "Clear");
      }

      void draw(Graphics2D g2d, AlphaComposite composite, String name) {
        GeneralPath dest = new GeneralPath();
        dest.moveTo(0, 0);
        dest.lineTo(100, 0);
        dest.lineTo(100, 100);
        dest.closePath();
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setColor(new Color(255, 0, 0, 190));
        g2d.fill(dest);

        g2d.setComposite(composite);
        g2d.setColor(new Color(0, 255, 0, 100));
        GeneralPath src = new GeneralPath();
        src.moveTo(0, 0);
        src.lineTo(100, 0);
        src.lineTo(0, 100);
        src.closePath();
        g2d.fill(src);

        g2d.setComposite(AlphaComposite.Src);
        g2d.setColor(Color.white);
        g2d.fillRect(40, 90, 60, 10);
        g2d.setColor(Color.black);
        g2d.drawString(name, 45, 100);
      }
    });

    tester.assertSame();
  }

  @Test
  public void panelPaintOverTest() throws Exception {
    URL url = VisualTest.class.getClassLoader().getResource("duke.gif");
    final BufferedImage image = ImageIO.read(url);
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        JComponent b = new JRadioButton("Foo");

        b.setSize(50, 20);

        Color c = new Color(250, 240, 220);
        c = new Color(255, 255, 255, 255);
        g2d.setColor(c);
        g2d.fillRect(50, 50, 50, 40);

        MetalIconFactory.getRadioButtonIcon().paintIcon(b, g2d, 60, 260);
        // g2d.drawImage(image, 1, 300, 60, 50, null);
        image.getHeight();

        g2d.setColor(c);
        g2d.fillRect(100, 100, 50, 40);
      }
    });

    tester.assertSame();
  }

  @Test
  public void copyPixelsTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setColor(Color.red);
        g2d.fillRect(50, 50, 30, 35);
        g2d.setColor(Color.blue);
        g2d.fillRect(70, 70, 20, 20);

        g2d.copyArea(60, 60, 25, 15, 50, 30);
      }
    });

    tester.assertSame();
  }

  @Test
  public void doLinesScaleTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.red);
        g2d.drawLine(3, 3, 50, 50);

        g2d.scale(10, 10);
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.blue);
        g2d.drawLine(8, 3, 55, 50);

        g2d.scale(0.1, 0.1);
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.green);
        g2d.drawLine(-2, 3, 45, 50);
      }
    });

    tester.assertSame();
  }

  @Test
  public void drawCheckboxIconTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        JCheckBox box = new JCheckBox();
        box.setSize(box.getPreferredSize());
        box.setSelected(true);
        box.paint(g2d);
      }
    });

    tester.assertSame();
  }

  @Test
  public void drawStippleTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, new float[] { 14, 40 }, 0));
        g2d.drawLine(0, 0, 50, 100);
        g2d.drawLine(10, 0, 60, 100);
        g2d.drawLine(20, 0, 70, 100);
        g2d.drawLine(10, 100, 0, 200);
        g2d.drawLine(20, 100, 10, 200);
        g2d.drawLine(30, 100, 20, 200);
      }
    });

    tester.assertSame();
  }

  @Test
  public void unknownSimplePolyTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(4));
        AffineTransform xform = AffineTransform.getShearInstance(0.2, 0.3);
        RoundRectangle2D rect = new RoundRectangle2D.Double(30, 40, 30, 60, 3, 7);

        // returned shape is a path, not an obvious simple poly
        Shape s = xform.createTransformedShape(rect);
        g2d.fill(s);

        xform = AffineTransform.getTranslateInstance(3, 2);
        s = xform.createTransformedShape(new Ellipse2D.Double(50, 120, 30, 20));
        g2d.fill(s);
      }
    });

    tester.assertSame();
  }
}
