package net.opengrabeso.glg2d.examples;

import javax.swing.*;
import java.awt.*;

public class UIDemoFrame extends JFrame {
    private final AFactory factory;
    public UIDemoFrame(AFactory factory) {
        this.factory = factory;
        setTitle("Swing Demo");
    }


    public void display() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        setContentPane(factory.createPanel(new UIDemo()));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1024, 768));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
