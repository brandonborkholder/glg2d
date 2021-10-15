package net.opengrabeso.glg2d.examples;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class G2DRoundCorners extends JComponent implements AnExample {
    @Override
    public String getTitle() {
        return "G2DRoundCorners";
    }

    private static final long serialVersionUID = 1L;

    private final int margin = 10;

        /*
        // using scale when creating the path affects the tesselator input (and tesselation)
        private final double preOffsetX = 735;
        private final double preOffsetY = 60;
        private final double preScale = 8;

        private final double offsetX = 0;
        private final double offsetY = 0;
        private final double scale = 1;
        */

    // using scale in G2D transform does not affect tesselation, this allows to zoom into artifacts without changing them
    private final double preOffsetX = 0;
    private final double preOffsetY = 0;
    private final double preScale = 1;

    private final double offsetX = 735;
    private final double offsetY = 60;
    private final double scale = 8;

    private final double[] coords = new double[]{
            789.7547, 65.55727, 788.2472, 67.11835, 786.17017, 68.0, 784.0, 68.0, 740.0, 68.0, 741.0, 67.0, 784.0, 67.0, 785.89886, 67.0, 787.7163, 66.22855, 789.0354, 64.86261
    };

    private final byte[] types = new byte[]{
            0, 3, 1, 1, 1, 3, 4
    };

    private double tx(double x) {
        return (x - preOffsetX) * preScale;
    }

    private double ty(double y) {
        return (y - preOffsetY) * preScale;
    }

    /**
     * Construct the path from data dumped from the debugger (coords and types)
     */
    private Path2D initPath() {
        Path2D.Double path = new Path2D.Double();
        int coordsPos = 0;
        double ox = offsetX;
        double oy = offsetY;
        for (int typesPos = 0; typesPos < types.length; typesPos++) {
            switch (types[typesPos]) {
                case PathIterator.SEG_MOVETO:
                    path.moveTo(tx(coords[coordsPos]), ty(coords[coordsPos + 1]));
                    coordsPos += 2;
                    break;
                case PathIterator.SEG_LINETO:
                    path.lineTo(tx(coords[coordsPos]), ty(coords[coordsPos + 1]));
                    coordsPos += 2;
                    break;
                case PathIterator.SEG_QUADTO:
                    path.quadTo(
                            tx(coords[coordsPos]), ty(coords[coordsPos + 1]),
                            tx(coords[coordsPos + 2]), ty(coords[coordsPos + 3])
                    );
                    coordsPos += 4;
                    break;
                case PathIterator.SEG_CUBICTO:
                    path.curveTo(
                            tx(coords[coordsPos]), ty(coords[coordsPos + 1]),
                            tx(coords[coordsPos + 2]), ty(coords[coordsPos + 3]),
                            tx(coords[coordsPos + 4]), ty(coords[coordsPos + 5])
                    );
                    coordsPos += 6;
                    break;
                case PathIterator.SEG_CLOSE:
                    path.closePath();
                    break;
            }
        }
        return path;
    }

    private final Path2D path = initPath();

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(100, 100);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 300);
    }

    @Override
    public void paintComponent(Graphics g0) {
        Graphics2D g2d = (Graphics2D) g0;
        int margin = 0;
        Dimension dim = getSize();
        super.paintComponent(g2d);
        g2d.setColor(Color.white);
        g2d.fillRect(margin, margin, dim.width - margin * 2, dim.height - margin * 2);

        g2d.setColor(Color.red);
        g2d.scale(scale, scale);
        g2d.translate(-offsetX, -offsetY);
        g2d.fill(path);
    }
}

