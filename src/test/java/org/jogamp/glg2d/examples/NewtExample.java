package org.jogamp.glg2d.examples;

import javax.swing.JComponent;

import org.jogamp.glg2d.GLG2DCanvas;
import org.jogamp.glg2d.GLG2DHeadlessListener;
import org.jogamp.glg2d.GLG2DSimpleEventListener;
import org.jogamp.glg2d.event.NewtMouseEventTranslator;
import org.jogamp.glg2d.newt.HackedToolkit;
import org.jogamp.glg2d.newt.NewtHiddenParent;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;

/**
 * Thanks to Dan Avila for helping with this code.
 */
public class NewtExample {
  public static void main(String[] args) {
    HackedToolkit.init();

    final GLWindow window = GLWindow.create(GLG2DCanvas.getDefaultCapabalities());
    window.setTitle("GLG2D Newt Example");
    window.setSize(300, 300);

    // Close when window quits
    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowDestroyed(WindowEvent e) {
        System.exit(0);
      }
    });

    JComponent comp = Example.createComponent();

    new NewtHiddenParent(window).setContent(comp);

    // Add the painting listener
    window.addGLEventListener(new GLG2DSimpleEventListener(comp));

    // Add the headless listener
    window.addGLEventListener(new GLG2DHeadlessListener(comp));

    // Add a mouse event translator
    window.addMouseListener(new NewtMouseEventTranslator(comp));

    window.setVisible(true);
    new Animator(window).start();
  }
}
