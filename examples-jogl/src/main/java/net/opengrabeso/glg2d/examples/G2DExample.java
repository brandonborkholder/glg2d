package net.opengrabeso.glg2d.examples;

import net.opengrabeso.glg2d.GLG2DPanel;

import java.awt.*;
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


    static class CustomComponent extends JComponent {

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

        private Rectangle placeRect(Rectangle rect) {
            Dimension dim = getSize();

            int wMax = dim.width - margin - rect.x;
            int hMax = dim.height - margin - rect.y;

            return new Rectangle(
                    rect.x,
                    rect.y,
                    Math.min(rect.width, wMax),
                    Math.min(rect.height, hMax)
            );
        }

        @Override
        public void paintComponent(Graphics g) {
            int margin = 10;
            Dimension dim = getSize();
            super.paintComponent(g);
            g.setColor(Color.red);
            g.fillRect(margin, margin, dim.width - margin * 2, dim.height - margin * 2);

            {
                Rectangle rect = placeRect(new Rectangle(20, 20, 300, 300));
                g.setColor(Color.yellow);
                g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 100, 100);
            }

            {
                Rectangle rect = placeRect(new Rectangle(100, 100, 100, 100));
                g.setColor(Color.green);
                g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20);
            }

            {
                Rectangle rect = placeRect(new Rectangle(300, 100, 200, 200));
                g.setColor(Color.blue);
                g.fillOval(rect.x, rect.y, rect.width, rect.height);

            }
        }
    }
}

