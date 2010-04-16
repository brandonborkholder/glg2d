package joglg2d;

import java.awt.Graphics2D;

import joglg2d.util.Painter;
import joglg2d.util.TestWindow;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author borkholder
 * @created Feb 6, 2010
 */
public class VisualTest {
  @Test
  public void lineTest() throws Exception {
    TestWindow tester = new TestWindow();
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.drawLine(10, 10, 50, 50);
      }
    });

    int result = tester.waitForInput();
    Assert.assertEquals(TestWindow.SAME, result);
  }
}
