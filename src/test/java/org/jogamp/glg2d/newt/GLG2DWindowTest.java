package org.jogamp.glg2d.newt;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.RepaintManager;

import org.jogamp.glg2d.GLG2DCanvas;
import org.jogamp.glg2d.GLG2DPanel;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;

/**
 * A simple test to demonstrate a very basic Swing Heirarchy rendered in an
 * accelerated window.
 * 
 * @author Dan Avila
 * 
 */
public abstract class GLG2DWindowTest
{
  public GLG2DWindowTest()
  {
    GLG2DWindow window = null;
    boolean java2d = false;
    if (!java2d) {
      HackedToolkit.init();

      GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
      caps.setDoubleBuffered(true);
      caps.setNumSamples(4);
      caps.setSampleBuffers(true);
      window = GLG2DWindow.create(caps);
    }

    JComponent pane = getContentPane();

    if (java2d) {
      JFrame f = new JFrame();
      f.setContentPane(pane);
      f.pack();
      f.setVisible(true);
    }

    if (!java2d) {
      window.setContentPane(pane);
      window.setSize(600, 600);
      window.setVisible(true);

      final Animator animator = new Animator();
      animator.setRunAsFastAsPossible(true);
      animator.add(window);

      Executors.newSingleThreadExecutor().execute(new Runnable()
      {
        @Override
        public void run()
        {
          animator.start();
        }
      });
    }
  }

  protected abstract JComponent getContentPane();

  private static void createAndShowGLG2DPanel()
  {
    JFrame window = new JFrame();

    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    GLG2DPanel panel = new GLG2DPanel();
    panel.setDrawableComponent(new ContentPane());

    window.setContentPane(panel);
    window.setSize(600, 600);

    window.setVisible(true);
  }
}
