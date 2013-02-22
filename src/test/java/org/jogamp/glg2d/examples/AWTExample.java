package org.jogamp.glg2d.examples;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.jogamp.glg2d.GLG2DCanvas;

public class AWTExample {
  public static void main(String[] args) {
    JFrame frame = new JFrame("GLG2D AWT Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(300, 300));

    JComponent comp = Example.createComponent();

    frame.setContentPane(new GLG2DCanvas(comp));

    frame.pack();
    frame.setVisible(true);
  }
}
