package org.jogamp.glg2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jogamp.glg2d.GLG2DPanel;

@SuppressWarnings("serial")
public class GraphTest extends JFrame {
  Random random = new Random();

  AffineTransform transform = new AffineTransform();

  long repaintCount;
  long firstRepaint;

  GraphTest() {
    final List<Vertex> vertices = makeVertices(1000, new Rectangle(1024, 768));
    final List<Edge> edges = makeEdges(vertices, 2);

    JPanel paintingComponent = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GraphTest.this.paint(vertices, edges, (Graphics2D) g);

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(45, getHeight() - 65, 70, 20);
        g.setColor(Color.BLACK);
        g.drawString(String.format("FPS: %.2f", getFPS()), 50, getHeight() - 50);
      }
    };
    paintingComponent.setOpaque(true);

//    setContentPane(paintingComponent);
     setContentPane(new GLG2DPanel(paintingComponent));

    MouseHandler handler = new MouseHandler();
    getContentPane().addMouseListener(handler);
    getContentPane().addMouseMotionListener(handler);
    getContentPane().addMouseWheelListener(handler);

    new Timer(10, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        getContentPane().repaint();
      }
    }).start();
  }

  private double getFPS() {
    if (firstRepaint == 0) {
      firstRepaint = System.currentTimeMillis();
      repaintCount = 0;
      return 0;
    }

    repaintCount++;

    long now = System.currentTimeMillis();
    double time = now - firstRepaint;

    return repaintCount / (time / 1000);
  }

  public static void main(String[] args) throws Exception {
    JFrame test = new GraphTest();
    test.setTitle("Graph Test");
    test.setPreferredSize(new Dimension(1024, 768));
    test.pack();
    test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    test.setLocationRelativeTo(null);
    test.setVisible(true);
  }

  public void paint(List<Vertex> vertices, List<Edge> edges, Graphics2D g2d) {
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    for (Edge edge : edges) {
      edge.paint(g2d);
    }

    for (Vertex vertex : vertices) {
      vertex.paint(g2d);
    }
  }

  List<Vertex> makeVertices(int count, Rectangle bounds) {
    int border = 30;

    List<Vertex> vertices = new ArrayList<Vertex>(count);
    for (int i = 0; i < count; i++) {
      Point position = new Point(
          random.nextInt(bounds.width - border * 2) + border,
          random.nextInt(bounds.height - border * 2) + border);
      String name = String.valueOf(random.nextLong());

      vertices.add(new Vertex(name, position));
    }

    return vertices;
  }

  List<Edge> makeEdges(List<Vertex> vertices, double density) {
    int numEdges = (int) (vertices.size() * density);
    List<Edge> edges = new ArrayList<Edge>(numEdges);

    for (int i = 0; i < numEdges; i++) {
      Vertex from = vertices.get(random.nextInt(vertices.size()));
      Vertex to = vertices.get(random.nextInt(vertices.size()));

      if (from == to) {
        i--;
        continue;
      } else {
        edges.add(new Edge(from, to));
      }
    }

    return edges;
  }

  class Vertex {
    String name;

    Point position;

    Vertex(String name, Point position) {
      this.name = name;
      this.position = position;
    }

    Point getPosition() {
      Point pt = new Point();
      transform.transform(position, pt);
      return pt;
    }

    void paint(Graphics2D g2d) {
      g2d.setStroke(new BasicStroke(3));

      int width = g2d.getFontMetrics().stringWidth(name);
      int height = g2d.getFontMetrics().getHeight();

      int border = 5;

      Point pt = getPosition();
      RoundRectangle2D shape = new RoundRectangle2D.Float(
          pt.x - width / 2 - border,
          pt.y - height - border,
          width + border * 2,
          height + border * 2,
          border, border);

      g2d.setColor(new Color(14, 180, 255, 180));
      g2d.fill(shape);

      g2d.setColor(Color.DARK_GRAY);
      g2d.draw(shape);

      g2d.drawString(name, pt.x - width / 2, pt.y);
    }
  }

  static class Edge {
    Vertex from;

    Vertex to;

    Edge(Vertex from, Vertex to) {
      this.from = from;
      this.to = to;
    }

    void paint(Graphics2D g2d) {
      Point fromPt = from.getPosition();
      Point toPt = to.getPosition();
      AffineTransform xform = AffineTransform.getTranslateInstance(fromPt.x, fromPt.y);

      float dx = toPt.x - fromPt.x;
      float dy = toPt.y - fromPt.y;
      float thetaRadians = (float) Math.atan2(dy, dx);
      xform.rotate(thetaRadians);
      float dist = (float) Math.sqrt(dx * dx + dy * dy);
      xform.scale(dist, 1.0);

      QuadCurve2D curve = new QuadCurve2D.Float(0, 0, 0, 1, 1, 1);
      Shape shape = xform.createTransformedShape(curve);

      g2d.setColor(Color.black);
      g2d.setStroke(new BasicStroke(2));
      g2d.draw(shape);
    }
  }

  class MouseHandler extends MouseAdapter implements MouseWheelListener {
    Point firstPoint;

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
      double scale = e.getWheelRotation() * 1.5;
      if (scale > 0) {
        scale = 1 / scale;
      } else {
        scale = -scale;
      }

      Point dst = e.getPoint();
      transform.transform(e.getPoint(), dst);

      AffineTransform xform = AffineTransform.getTranslateInstance(dst.x, dst.y);
      xform.scale(scale, scale);
      xform.translate(-dst.x, -dst.y);
      transform.preConcatenate(xform);

      repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
      firstPoint = null;

      if ((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) != 0) {
        firstPoint = e.getPoint();
      }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      if (firstPoint != null && (e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) != 0) {
        Point dst = new Point();
        try {
          transform.inverseTransform(firstPoint, firstPoint);
          transform.inverseTransform(e.getPoint(), dst);
        } catch (NoninvertibleTransformException ex) {
          ex.printStackTrace();
        }

        int dx = dst.x - firstPoint.x;
        int dy = dst.y - firstPoint.y;
        transform.translate(dx, dy);

        firstPoint = e.getPoint();
      }

      repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          transform.scale(2, 2);
        } else {
          transform.scale(.5, .5);
        }
      }
      repaint();
    }
  }
}
