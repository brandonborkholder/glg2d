package org.jogamp.glg2d;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;

public class NewtTest {
  public static void main(String[] args) throws InterruptedException {
    GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
    caps.setHardwareAccelerated(true);
    caps.setDoubleBuffered(true);

    Display display = NewtFactory.createDisplay(null);
    Screen screen = NewtFactory.createScreen(display, 0);
    GLWindow glWindow = GLWindow.create(screen, caps);

    glWindow.setTitle("NEWT Test");
    glWindow.setSize(1024, 768);
    glWindow.setUndecorated(false);
    glWindow.setAlwaysOnTop(false);
    glWindow.setFullscreen(false);
    glWindow.setPointerVisible(true);
    glWindow.confinePointer(false);

    JComponent comp = createComponent();

    comp.setBounds(0, 0, 400, 400);
    comp.doLayout();
    comp.addNotify();
    glWindow.addGLEventListener(new GLG2DSimpleEventListener(comp));

    Animator animator = new Animator();
    animator.setRunAsFastAsPossible(true);
    animator.add(glWindow);
    animator.start();

    glWindow.setVisible(true);
    animator.setUpdateFPSFrames(60, System.err);

    while (glWindow.isVisible()) {
      Thread.sleep(100);
    }

    animator.stop();
    glWindow.destroy();
    System.exit(0);
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
}
