package net.opengrabeso.glg2d.examples;

import javax.swing.*;
import java.awt.*;

public class AWTExample {
    private final AFactory factory;

    public AWTExample(AFactory factory) {
        this.factory = factory;
    }

    public void display() {
        JFrame frame = new JFrame("GLG2D AWT Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(300, 300));

        JComponent comp = Example.createComponent();

        frame.setContentPane(factory.createPanel(comp));

        frame.pack();
        frame.setVisible(true);
    }
}
