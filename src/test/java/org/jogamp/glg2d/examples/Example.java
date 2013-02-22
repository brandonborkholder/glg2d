package org.jogamp.glg2d.examples;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

class Example {
  public static JComponent createComponent() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setDoubleBuffered(false);

    panel.add(new JButton("Press me!"), BorderLayout.NORTH);

    JProgressBar bar = new JProgressBar();
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
