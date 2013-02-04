package org.jogamp.glg2d.examples;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import org.jogamp.glg2d.GLG2DCanvas;
import org.jogamp.glg2d.GLG2DHeadlessListener;
import org.jogamp.glg2d.GLG2DSimpleEventListener;
import org.jogamp.glg2d.event.AWTMouseEventTranslator;

import com.jogamp.opengl.util.Animator;

public class AWTExample {
  public static void main(String[] args) {
    JFrame frame = new JFrame("GLG2D AWT Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(300, 300));

    JComponent comp = Example.createComponent();

    GLCanvas canvas = new GLCanvas(GLG2DCanvas.getDefaultCapabalities());
    canvas.setEnabled(false);
    JPanel p = new JPanel(new BorderLayout());
    p.add(canvas, BorderLayout.CENTER);

    // Put into a JRootPane if the component has no Window ancestor
    JRootPane root = new JRootPane();
    root.setContentPane(comp);

    // Add the painting listener
    canvas.addGLEventListener(new GLG2DSimpleEventListener(comp));

    // Add the headless listener
    canvas.addGLEventListener(new GLG2DHeadlessListener(comp));

    // Add a mouse event translator
    AWTMouseEventTranslator mouseEvtTranslator = new AWTMouseEventTranslator(comp);
    p.addMouseListener(mouseEvtTranslator);
    p.addMouseMotionListener(mouseEvtTranslator);
    p.addMouseWheelListener(mouseEvtTranslator);

    new Animator(canvas).start();

    frame.setContentPane(p);

    frame.pack();
    frame.setVisible(true);
  }
}
