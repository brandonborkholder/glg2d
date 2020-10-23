package net.opengrabeso.glg2d.examples;

import net.opengrabeso.glg2d.GLG2DPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;

// adapted from https://stackoverflow.com/a/6263897/16673

public class G2DExample extends JFrame {

    private static final long serialVersionUID = 1L;

    public G2DExample() {
        setTitle("Graphics2D rendering example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void display() {
        JComponent comp = new CustomComponent();
        setContentPane(new GLG2DPanel(comp));
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        G2DExample main = new G2DExample();
        main.display();
    }
}

class CustomComponent extends JComponent {

    private static final long serialVersionUID = 1L;

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(100, 100);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 300);
    }

    @Override
    public void paintComponent(Graphics g) {
        int margin = 10;
        Dimension dim = getSize();
        super.paintComponent(g);
        g.setColor(Color.red);
        g.fillRect(margin, margin, dim.width - margin * 2, dim.height - margin * 2);

        g.setColor(Color.green);

        int x = 100;
        int y = 100;
        int wMax = dim.width - margin - x;
        int hMax = dim.height - margin - y;

        int w = Math.min(100, wMax);
        int h = Math.min(100, hMax);

        g.fillRoundRect(100, 100, w, h, 20 , 20);
    }
}