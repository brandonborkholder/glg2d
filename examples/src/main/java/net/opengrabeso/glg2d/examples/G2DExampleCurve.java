package net.opengrabeso.glg2d.examples;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

// adapted from https://stackoverflow.com/a/6263897/16673

public class G2DExampleCurve extends JComponent implements AnExample {
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

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        Path2D.Double shape = new Path2D.Double();
        float scale = 1.33f;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.scale(scale, scale * 1.2f);
        g2d.translate(-320, -160);
        g2d.setStroke(new BasicStroke(2.0f / scale));

        shape.moveTo(354,189);
        shape.curveTo(356.3333333333333,188.66666666666666,356.0,189.66666666666666,357.0,188.0);
        shape.curveTo(358.0,186.33333333333334,357.6666666666667,186.0,357.0,184.0);
        shape.curveTo(356.3333333333333,182.0,357.0,182.66666666666666,355.0,182.0);
        shape.curveTo(353.0,181.33333333333334,352.6666666666667,181.0,351.0,182.0);
        shape.curveTo(349.3333333333333,183.0,349.0,184.0,350.0,185.0);
        shape.curveTo(351.0,186.0,353.6666666666667,184.66666666666666,354.0,185.0);
        shape.curveTo(354.3333333333333,185.33333333333334,352.3333333333333,184.66666666666666,351.0,186.0);
        shape.curveTo(349.6666666666667,187.33333333333334,349.0,188.0,350.0,189.0);
        shape.curveTo(351.0,190.0,351.6666666666667,189.33333333333334,354.0,189.0);

        g2d.setColor(Color.BLACK);
        g2d.draw(shape);
    }
}

