package net.opengrabeso.glg2d.examples;

import net.opengrabeso.glg2d.GLG2DPanel;

import javax.swing.*;

public class JoglExampleFactory {
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    public static void display(JComponent component) {

        GLG2DPanel panel = new GLG2DPanel(component);
        panel.setPreferredSize(component.getPreferredSize());
        panel.setMinimumSize(component.getMinimumSize());

        JFrame frame = new JFrame(((AnExample)component).getTitle());

        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        frame.setContentPane(new GLG2DPanel(component));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(component.getPreferredSize());
        frame.setMinimumSize(component.getMinimumSize());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}
