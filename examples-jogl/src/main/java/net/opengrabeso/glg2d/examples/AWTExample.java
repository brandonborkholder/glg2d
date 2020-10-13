package net.opengrabeso.glg2d.examples;

import net.opengrabeso.glg2d.GLG2DPanel;

import javax.swing.*;
import java.awt.*;

public class AWTExample {
  public static void main(String[] args) {
    JFrame frame = new JFrame("GLG2D AWT Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(300, 300));

    JComponent comp = Example.createComponent();

    frame.setContentPane(new GLG2DPanel(comp));

    frame.pack();
    frame.setVisible(true);
  }
}
