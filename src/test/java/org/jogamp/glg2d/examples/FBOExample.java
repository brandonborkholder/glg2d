package org.jogamp.glg2d.examples;

import static javax.media.opengl.GLDrawableFactory.getFactory;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.JComponent;
import javax.swing.JRootPane;

import org.jogamp.glg2d.GLG2DCanvas;
import org.jogamp.glg2d.GLG2DHeadlessListener;
import org.jogamp.glg2d.GLG2DSimpleEventListener;

public class FBOExample {
  public static void main(String[] args) throws InterruptedException {
    int size = 250;

    GLCapabilities caps = GLG2DCanvas.getDefaultCapabalities();
    caps.setFBO(true);
    caps.setOnscreen(false);
    GLAutoDrawable offscreen = getFactory(GLProfile.getGL2ES1()).createOffscreenAutoDrawable(null, caps, null, size, size);

    JComponent comp = Example.createComponent();

    // Put into a JRootPane if the component has no Window ancestor
    JRootPane root = new JRootPane();
    root.setContentPane(comp);

    // Add the painting listener
    offscreen.addGLEventListener(new GLG2DSimpleEventListener(comp));

    // Add the headless listener
    offscreen.addGLEventListener(new GLG2DHeadlessListener(comp));

    offscreen.display();

    System.exit(0);
  }
}
