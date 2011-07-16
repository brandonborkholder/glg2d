package misc;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.swing.JFrame;

import joglg2d.SimplePathVisitor;

import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleTimingTests {
  static final int NUM_TESTS = 100000;

  static GLAutoDrawable drawable;

  @BeforeClass
  public static void setup() {
    drawable = new GLJPanel();
    JFrame frame = new JFrame();
    frame.setContentPane((GLJPanel) drawable);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(new Dimension(50, 50));
    frame.setVisible(true);
  }

  @Test
  public void getMatrix() {
    Drawer d = new Drawer() {
      @Override
      public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();

        double height = 10;
        double[] matrix = new double[16];
        long start = System.nanoTime();
        for (int i = 0; i < NUM_TESTS; i++) {
          gl.glMatrixMode(GL.GL_MODELVIEW);
          gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, matrix, 0);
          new AffineTransform(matrix[0], -matrix[1], -matrix[4], matrix[5], matrix[12], height - matrix[13]);
        }
        long stop = System.nanoTime();

        double perCycle = (stop - start) / (double) NUM_TESTS;
        System.out.println("Get MODELVIEW matrix: " + perCycle + "ns per call");
      }
    };

    drawable.addGLEventListener(d);
    drawable.display();
    drawable.removeGLEventListener(d);
  }

  @Test
  public void cloneAffineTransform() {
    AffineTransform transform = new AffineTransform();
    transform.scale(1, 9);
    transform.translate(9, 49);
    transform.shear(4, 384);
    long start = System.nanoTime();
    for (int i = 0; i < NUM_TESTS; i++) {
      transform.clone();
    }
    long stop = System.nanoTime();

    double perCycle = (stop - start) / (double) NUM_TESTS;
    System.out.println("Clone AffineTransform: " + perCycle + "ns per call");
  }

  @Test
  public void naiveQuadraticBezier() {
    Random rand = new Random();
    float[] control = new float[6];

    long start = System.nanoTime();
    for (int i = 0; i < NUM_TESTS; i++) {
      for (int j = 0; j < 6; j++) {
        control[j] = rand.nextFloat();
      }

      float stepSize = 1F / 15;
      float[] p = new float[2];
      for (float t = 0; t <= 1; t += stepSize ) {
        float tt = t * t;
        float tMinusT = t * (1-t);
        float minusTminusT = (1-t) * (1-t);

        p[0] = minusTminusT * control[0] + 2 * tMinusT * control[2] + tt * control[4];
        p[1] = minusTminusT * control[1] + 2 * tMinusT * control[3] + tt * control[5];
      }
    }
    long stop = System.nanoTime();

    double perCycle = (stop - start) / (double) NUM_TESTS;
    System.out.println("Naive quadratic Bezier: " + perCycle + "ns per call");
  }

  @Test
  public void forwardDifferencingBezier() {
    SimplePathVisitor visitor = new SimplePathVisitor() {
      @Override
      public void setGLContext(GL context) {
      }
      
      @Override
      public void moveTo(float[] vertex) {
      }

      @Override
      public void lineTo(float[] vertex) {
      }

      @Override
      public void endPoly() {
      }

      @Override
      public void closeLine() {
      }

      @Override
      public void beginPoly(int windingRule) {
      }
    };

    Random rand = new Random();
    float[] previous = new float[2];
    float[] control = new float[4];

    long start = System.nanoTime();
    for (int i = 0; i < NUM_TESTS; i++) {
      previous[0] = rand.nextFloat();
      previous[1] = rand.nextFloat();
      for (int j = 0; j < 4; j++) {
        control[j] = rand.nextFloat();
      }

      visitor.quadTo(previous, control);
    }
    long stop = System.nanoTime();

    double perCycle = (stop - start) / (double) NUM_TESTS;
    System.out.println("Forward differencing Bezier: " + perCycle + "ns per call");
  }

  static abstract class Drawer implements GLEventListener {
    @Override
    public void init(GLAutoDrawable drawable) {
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    @Override
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
  }
}
