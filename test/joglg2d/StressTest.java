package joglg2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import joglg2d.util.CustomPainter;
import joglg2d.util.TestWindow;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author borkholder
 * @created Apr 28, 2010
 */
public class StressTest {
  static final long TESTINGTIME = 3000;

  static TestWindow tester;

  static Random rand = new Random();

  @BeforeClass
  public static void initialize() {
    tester = new TestWindow();
  }

  @AfterClass
  public static void close() {
    tester.close();
  }

  @Test
  public void shapeTest() throws Exception {
    TimedPainter painter = new TimedPainter() {
      @Override
      protected void paint(Graphics2D g2d) {
        g2d.setColor(Color.red);
        Rectangle2D.Float rect = new Rectangle2D.Float();
        float w = 20;
        float h = 40;
        float x = 300;
        float y = 400;
        for (int i = 0; i < 1000; i++) {
          rect.setRect(rand.nextFloat() * x, rand.nextFloat() * y, rand.nextFloat() * w, rand.nextFloat() * h);
          g2d.fill(rect);
        }
      }
    };

    tester.setPainter(painter);
    painter.waitAndLogTimes("shapes");
  }

  @Test
  public void lineTest() throws Exception {
    TimedPainter painter = new TimedPainter() {
      @Override
      protected void paint(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        int x = 300;
        int y = 400;
        int numpoints = 5;
        int[] xarray = new int[numpoints];
        int[] yarray = new int[numpoints];
        for (int i = 0; i < 100; i++) {
          for (int j = 0; j < numpoints; j++) {
            xarray[j] = rand.nextInt(x);
            yarray[j] = rand.nextInt(y);
          }

          g2d.drawPolyline(xarray, yarray, numpoints);
        }
      }
    };

    tester.setPainter(painter);
    painter.waitAndLogTimes("lines");
  }

  static abstract class TimedPainter implements CustomPainter {
    long[] times = new long[2];

    int[] calls = new int[2];

    @Override
    public void paint(Graphics2D g2d, boolean jogl) {
      long start = System.nanoTime();
      paint(g2d);
      long end = System.nanoTime();

      int index = jogl ? 0 : 1;
      times[index] += end - start;
      calls[index]++;
    }

    protected abstract void paint(Graphics2D g2d);

    public void waitAndLogTimes(String type) throws InterruptedException {
      Thread.sleep(TESTINGTIME);

      System.out.println(String.format("JOGL for %s took an average of %.3f ms", type, times[0] / 1e6 / calls[0]));
      System.out.println(String.format("Java2D for %s took an average of %.3f ms", type, times[1] / 1e6 / calls[1]));
    }
  }
}
