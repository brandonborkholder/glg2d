package joglg2d.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import joglg2d.JOGLPanel;

public class SwingTest {
  public static void main(String[] args) throws Exception {
    JFrame frame = new JFrame("JOGLG2D");
    frame.setContentPane(new JOGLPanel(new BorderLayout()));
    configureFrame(frame);

    frame = new JFrame("Graphics2D");
    frame.setContentPane(new JPanel(new BorderLayout()));
    configureFrame(frame);
  }

  protected static void configureFrame(JFrame frame) {
//    frame.add(new JLabel("<html><h3><em>hi!</em></h3></html>"), BorderLayout.NORTH);
    URL url = SwingTest.class.getClassLoader().getResource("duke.gif");
//    frame.add(new JButton("Foo", new ImageIcon(url)), BorderLayout.WEST);

//    JPanel panel = new JPanel(new GridLayout(2, 1));
//    panel.add(new JRadioButton("press"));
//    panel.add(new JCheckBox("press"));
    frame.add(new JRadioButton("foo"), BorderLayout.NORTH);

    frame.setPreferredSize(new Dimension(800, 400));
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
