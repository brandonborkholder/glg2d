package net.opengrabeso.glg2d.examples;

import net.opengrabeso.glg2d.GLG2DPanel;

import javax.swing.*;
import java.awt.*;

public class SwingExampleFactory {
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    public static void display(JComponent component) {
        JFrame frame = new JFrame(((AnExample)component).getTitle());

        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        frame.setContentPane(component);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
