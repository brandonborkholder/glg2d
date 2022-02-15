package net.opengrabeso.glg2d.examples;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

// adapted from https://stackoverflow.com/a/6263897/16673

public class G2DExampleCircles extends JComponent implements AnExample {
    @Override
    public String getTitle() {
        return "G2DExample";
    }

    private static final long serialVersionUID = 1L;

    private final int margin = 10;

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(100, 100);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 300);
    }

    private void drawCircleUsingArc(Graphics2D g2d, int x, int y, int w, int h, int segments) {
        int step = 360 / segments;
        for (int angle = 0; angle < 360; angle += step) {
            g2d.fillArc(x, y, w, h, angle, step);
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        Path2D.Double shape = new Path2D.Double();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLUE);
        for (int size = 2; size < 20; size ++) {
            g2d.fillOval(10 + size * 20, 20 - size / 2, size, size);
        }
        g2d.setColor(Color.GREEN.darker());
        for (int size = 2; size < 20; size ++) {
            drawCircleUsingArc(g2d, 10 + size * 20, 45 - size / 2, size, size, 4);
        }
        g2d.setColor(Color.ORANGE.darker());
        int xx = 10;
        for (int size = 2; size < 30; size ++) {
            // this is important case, as this way Flying Saucer creates round borders
            drawCircleUsingArc(g2d, xx, 80 - size / 2, size, size, 8);
            xx += size + 5;
        }
        int y = 140;
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(30, y - 20, 40, 40);
        g2d.setColor(Color.RED);
        g2d.fillOval(40, y - 10, 20, 20);
        g2d.setColor(Color.ORANGE);
        g2d.fillOval(45, y - 5, 10, 10);

        g2d.setColor(Color.BLACK);
        g2d.draw(shape);


    }
}

