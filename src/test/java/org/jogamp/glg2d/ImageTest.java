package org.jogamp.glg2d;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawable;
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
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.GLAutoDrawableDelegate;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.Screenshot;

public class ImageTest {
  static JLabel icon;
  
  public static void main(String[] args) throws InterruptedException {
    GLCapabilities caps = GLG2DCanvas.getDefaultCapabalities();
    caps.setFBO(true);
    caps.setOnscreen(false);
    GLAutoDrawable offscreen = GLDrawableFactory.getFactory(GLProfile.getGL2ES1()).createOffscreenAutoDrawable(null, caps, null, 200, 200, null);
    
    JComponent comp = createComponent();

    comp.setBounds(0, 0, 200, 200);
    comp.doLayout();
    comp.addNotify();
    offscreen.addGLEventListener(new GLG2DSimpleEventListener(comp));
    offscreen.addGLEventListener(new ImageCopier());

    icon = new JLabel();
    
    JFrame frame = new JFrame("Image test");
    frame.setContentPane(icon);
    frame.setPreferredSize(new Dimension(300, 300));
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    
    FPSAnimator animator = new FPSAnimator(10);
    animator.add(offscreen);
    animator.start();
  }

  private static JComponent createComponent() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setDoubleBuffered(false);

    panel.add(new JButton("Press me!"), BorderLayout.NORTH);

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
      final BufferedImage img = Screenshot.readToBufferedImage(200, 200);
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
