package joglg2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GraphTest extends JFrame {
  Random random = new Random();

  AffineTransform transform = new AffineTransform();

  GraphTest() {
    final List<Vertex> vertices = makeVertices(1000, new Rectangle(1024, 768));
    final List<Edge> edges = makeEdges(vertices, 2);

    setContentPane(new JOGLPanel() {
      @Override
      protected void paintGL(JOGLG2D g2d) {
        GraphTest.this.paint(vertices, edges, (Graphics2D) g2d);
      }
    });

    MouseHandler handler = new MouseHandler();
    getContentPane().addMouseListener(handler);
    getContentPane().addMouseMotionListener(handler);
    getContentPane().addMouseWheelListener(handler);
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
    AffineTransform old = g2d.getTransform();
    g2d.setTransform(transform);
    
    for (Edge edge : edges) {
      edge.paint(g2d);
    }

    for (Vertex vertex : vertices) {
      vertex.paint(g2d);
    }
    
    g2d.setTransform(old);
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

  static class Vertex {
    String name;

    Point position;

    Vertex(String name, Point position) {
      this.name = name;
      this.position = position;
    }

    void paint(Graphics2D g2d) {
      g2d.setStroke(new BasicStroke(3));

      int width = g2d.getFontMetrics().stringWidth(name);
      int height = g2d.getFontMetrics().getHeight();

      int border = 5;

      RoundRectangle2D shape = new RoundRectangle2D.Float(
          position.x - width / 2 - border,
          position.y - height - border,
          width + border * 2,
          height + border * 2,
          border, border);

      g2d.setColor(new Color(14, 180, 255, 180));
      g2d.fill(shape);

      g2d.setColor(Color.DARK_GRAY);
      g2d.draw(shape);

      g2d.drawString(name, position.x - width / 2, position.y);
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
      AffineTransform xform = AffineTransform.getTranslateInstance(from.position.x, from.position.y);

      float dx = to.position.x - from.position.x;
      float dy = to.position.y - from.position.y;
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
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
      int scale = e.getWheelRotation();
      if (scale < 0) {
        scale = -1 / scale;
      }

      transform.scale(scale, scale);
      repaint();
    }

    Point firstPoint;

    @Override
    public void mousePressed(MouseEvent e) {
      firstPoint = null;

      if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
        firstPoint = e.getPoint();
      }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      if (firstPoint != null && (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
        int dx = e.getPoint().x - firstPoint.x;
        int dy = e.getPoint().y - firstPoint.y;
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
