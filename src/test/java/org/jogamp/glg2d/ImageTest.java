package org.jogamp.glg2d;

import static java.lang.Math.max;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jogamp.glg2d.event.AWTMouseEventTranslator;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;

public class ImageTest {
  static JLabel icon;
  static int size = 250;

  public static void main(String[] args) throws InterruptedException {
    GLCapabilities caps = GLG2DCanvas.getDefaultCapabalities();
    caps.setFBO(true);
    caps.setOnscreen(false);
    GLAutoDrawable offscreen = GLDrawableFactory.getFactory(GLProfile.getGL2ES1()).createOffscreenAutoDrawable(null, caps, null, size, size);

    JComponent comp = createComponent();

    JRootPane p = new JRootPane();
    p.setContentPane(comp);

    offscreen.addGLEventListener(new GLG2DSimpleEventListener(comp));
    offscreen.addGLEventListener(new GLG2DHeadlessListener(comp));
    offscreen.addGLEventListener(new ImageCopier());

    icon = new JLabel();

    final JFrame frame = new JFrame("Image test");
    JPanel panel = new JPanel(null);
    panel.add(icon);
    icon.setBounds(0, 0, size, size);
    frame.setContentPane(panel);
    frame.setPreferredSize(new Dimension(size * 5, size * 3));
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);

    final AWTMouseEventTranslator translator = new AWTMouseEventTranslator(comp);
    panel.addMouseListener(translator);
    panel.addMouseMotionListener(translator);
    panel.addMouseWheelListener(translator);

    Timer t = new Timer(5000, new ActionListener() {
      Random r = new Random();

      @Override
      public void actionPerformed(ActionEvent e) {
        icon.setBounds(r.nextInt(max(1, frame.getWidth() - size)),
            r.nextInt(max(1, frame.getHeight() - size)), size, size);

        translator.setOriginToTargetTransform(AffineTransform.getTranslateInstance(-icon.getX(), -icon.getY()));
      }
    });
    t.setRepeats(true);
    t.start();

    FPSAnimator animator = new FPSAnimator(5);
    animator.add(offscreen);
    animator.start();
  }

  private static JComponent createComponent() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setDoubleBuffered(false);

    panel.add(new JButton("Press me!"), BorderLayout.NORTH);

    JLabel txt = new JLabel("<html>This is an image being painted at 5Hz. The source component is offscreen and not visible anywhere. " +
        "The mouse events are being registered with the visible frame, translated and then re-sent to the offscreen " +
        "component.  The offscreen component handles them, paints itself to the an FBO, which is captured into a " +
        "BufferedImage, which is then painted here");

    panel.add(txt, BorderLayout.CENTER);

    JProgressBar bar = new JProgressBar();
    bar.setIndeterminate(true);
    panel.add(bar, BorderLayout.SOUTH);
    panel.add(new JSlider(SwingConstants.VERTICAL, 0, 10, 3), BorderLayout.EAST);

    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    return panel;
  }

  static class ImageCopier implements GLEventListener {
    @Override
    public void init(GLAutoDrawable drawable) {
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void display(GLAutoDrawable drawable) {
      AWTGLReadBufferUtil util = new AWTGLReadBufferUtil(GLG2DCanvas.getDefaultCapabalities().getGLProfile(), false);
      final BufferedImage img = util.readPixelsToBufferedImage(drawable.getContext().getGL(), true);
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          icon.setIcon(new ImageIcon(img));
        }
      });
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }
  }
}
