package glg2d;

import java.awt.Graphics2D;

import glg2d.util.Painter;
import glg2d.util.TestWindow;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TinyDifferences {
  static TestWindow tester;

  @BeforeClass
  public static void initialize() {
    tester = new TestWindow();
  }

  @AfterClass
  public static void close() {
    tester.finish();
  }

  @Test
  public void lineTest() throws Exception {
    tester.setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g) {
        int controlSize = 13;
        int x = 5;
        int y = 5;
        g.fillRect(x + 3, y + 5, 2, controlSize - 8);
//        g.drawLine(x + (controlSize - 4), y + 3, x + 5, y + (controlSize - 6));
//        g.drawLine(x + (controlSize - 4), y + 4, x + 5, y + (controlSize - 5));
        g.drawLine(x + 5, y + (controlSize - 6), x + (controlSize - 4), y + 3);
        g.drawLine(x + 5, y + (controlSize - 5), x + (controlSize - 4), y + 4);
      }
    });

    tester.assertSame();
  }
}
