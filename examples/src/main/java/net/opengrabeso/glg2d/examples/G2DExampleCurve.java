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
        float scale = 5;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.scale(scale, scale);
        g2d.translate(-1200, -200);
        g2d.setStroke(new BasicStroke(2.0f / scale));

        shape.moveTo(1227, 240);
        shape.curveTo(1228.0, 241.0, 1228.0, 240.0, 1230.0, 240.0);
        shape.curveTo(1232.0, 240.0, 1231.0, 240.0, 1233.0, 240.0);
        shape.curveTo(1235.0, 240.0, 1234.0, 240.0, 1236.0, 240.0);
        shape.curveTo(1238.0, 240.0, 1237.0, 240.0, 1239.0, 240.0);
        shape.curveTo(1241.0, 240.0, 1240.0, 240.0, 1242.0, 240.0);
        shape.curveTo(1244.0, 240.0, 1244.0, 241.0, 1245.0, 240.0);
        shape.curveTo(1246.0, 239.0, 1246.0, 238.0, 1245.0, 237.0);
        shape.curveTo(1244.0, 236.0, 1244.0, 237.0, 1242.0, 237.0);
        shape.curveTo(1240.0, 237.0, 1240.0, 238.0, 1239.0, 237.0);
        shape.curveTo(1238.0, 236.0, 1240.0, 235.0, 1239.0, 234.0);
        shape.curveTo(1238.0, 233.0, 1237.0, 235.0, 1236.0, 234.0);
        shape.curveTo(1235.0, 233.0, 1237.0, 232.0, 1236.0, 231.0);
        shape.curveTo(1235.0, 230.0, 1235.0, 231.0, 1233.0, 231.0);
        shape.curveTo(1231.0, 231.0, 1231.0, 230.0, 1230.0, 231.0);
        shape.curveTo(1229.0, 232.0, 1231.0, 233.0, 1230.0, 234.0);
        shape.curveTo(1229.0, 235.0, 1228.0, 233.0, 1227.0, 234.0);
        shape.curveTo(1226.0, 235.0, 1227.0, 235.0, 1227.0, 237.0);
        shape.curveTo(1227.0, 239.0, 1226.0, 239.0, 1227.0, 240.0);

        g2d.setColor(Color.BLACK);
        g2d.draw(shape);
    }
}

