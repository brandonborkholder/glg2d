package joglg2d;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Test;

public class MovePanelTest {
  @Test
  public void testMovePanel() throws Exception {
    JFrame frame = new JFrame();
    JPanel painter = new JPanel() {
      protected void paintComponent(Graphics g) {
        g.setColor(Color.blue);
        g.drawRect(3, 4, 9, 18);
        g.setColor(Color.red);
        g.fillRect(30, 18, 99, 20);
      }
    };
    G2DGLCanvas canvas = new G2DGLCanvas(painter);
    frame.setContentPane(canvas);
    frame.setPreferredSize(new Dimension(150, 150));
    frame.setTitle("Frame 1");
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    Thread.sleep(1 * 1000);
    frame.setVisible(false);
    frame.dispose();
    
    frame = new JFrame();
    frame.setContentPane(canvas);
    frame.setPreferredSize(new Dimension(150, 150));
    frame.setTitle("Frame 2");
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    Thread.sleep(2 * 1000);
  }
}
