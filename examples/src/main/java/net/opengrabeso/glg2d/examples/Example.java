package net.opengrabeso.glg2d.examples;

import net.opengrabeso.glg2d.GLGraphics2D;

import javax.swing.*;
import java.awt.*;

class Example {
  public static JComponent createComponent() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setDoubleBuffered(false);

    panel.add(new JButton("Press me!"), BorderLayout.NORTH);

    JProgressBar bar = new JProgressBar() {
      protected void paintComponent(java.awt.Graphics g) {
       if (g instanceof GLGraphics2D
           ) {
         super.paintComponent(g);
       } else {
         System.out.println(g.getClass());
       }
      }
    };
    bar.setIndeterminate(true);
    panel.add(bar, BorderLayout.SOUTH);
    panel.add(new JSlider(SwingConstants.VERTICAL, 0, 10, 3), BorderLayout.EAST);

    ButtonGroup grp = new ButtonGroup();
    JRadioButton radio1 = new JRadioButton("FM");
    JRadioButton radio2 = new JRadioButton("AM");
    grp.add(radio1);
    grp.add(radio2);

    JPanel panel2 = new JPanel(new GridLayout(0, 1));
    panel2.add(radio1);
    panel2.add(radio2);
    
    JComboBox b = new JComboBox(new String[] {"3", "4"});

    panel.add(b, BorderLayout.WEST);

    panel.setBorder(BorderFactory.createTitledBorder("Border"));

    return panel;
  }
}
