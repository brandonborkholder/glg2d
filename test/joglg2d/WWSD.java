package joglg2d;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import joglg2d.util.Painter;

public class WWSD {
  public static void main(String[] args) throws Exception {
    drawImage();
  }

  static void drawImage() throws Exception {
    URL url = VisualTest.class.getClassLoader().getResource("duke.gif");
    final BufferedImage image = ImageIO.read(url);
    paint(new Painter() {
      @Override
      public void paint(Graphics2D g2d) {
        g2d.drawImage(image, 200, 400, 20, 40, 5, 20, 200, 400, null, null);
      }
    });
  }

  @SuppressWarnings("serial")
  static void paint(final Painter painter) {
    JFrame frame = new JFrame("What Would Swing Do?");
    frame.setContentPane(new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        painter.paint((Graphics2D) g);
      }
    });
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(400, 400));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
