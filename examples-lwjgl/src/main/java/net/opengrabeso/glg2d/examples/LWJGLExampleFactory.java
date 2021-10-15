package net.opengrabeso.glg2d.examples;

import net.opengrabeso.glg2d.GLG2DPanelLWJGL;

import javax.swing.*;

public class LWJGLExampleFactory {
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    public static void display(JComponent component) {
        String title = ((AnExample)component).getTitle();
        new GLG2DPanelLWJGL(component, title).run();
    }
}

