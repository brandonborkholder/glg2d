package joglg2d.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import joglg2d.G2DGLCanvas;

public class SwingTest {
  public static void main(String[] args) throws Exception {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    JFrame frame = new JFrame("JOGLG2D");
    frame.setContentPane(new G2DGLCanvas(new JPanel(new BorderLayout())));
    configureFrame(frame);

    frame = new JFrame("Graphics2D");
    frame.setContentPane(new JPanel(new BorderLayout()));
    configureFrame(frame);
  }

  protected static void configureFrame(JFrame frame) {
    frame.add(new JLabel("<html><h3><em>hi!</em></h3></html>"), BorderLayout.NORTH);
    URL url = SwingTest.class.getClassLoader().getResource("duke.gif");
    frame.add(new JButton("Foo", new ImageIcon(url)), BorderLayout.WEST);

    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JRadioButton("press"));
    panel.add(new JCheckBox("press"));
    panel.add(new JSpinner());
    panel.add(new JTextField());
    panel.add(new JComboBox(new Object[] { "foo", "bar" }));
    panel.add(new JTextArea());
    panel.add(new JScrollPane(new JTextArea()));
    frame.add(panel, BorderLayout.CENTER);

    frame.setPreferredSize(new Dimension(800, 400));
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
