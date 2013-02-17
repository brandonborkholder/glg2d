package org.jogamp.glg2d.examples;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.peer.FramePeer;
import java.lang.reflect.Field;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.PopupFactory;
import javax.swing.RepaintManager;

import org.jogamp.glg2d.GLG2DCanvas;
import org.jogamp.glg2d.GLG2DHeadlessListener;
import org.jogamp.glg2d.GLG2DSimpleEventListener;
import org.jogamp.glg2d.event.NewtMouseEventTranslator;

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
    
    System.setProperty("swing.handleTopLevelPaint", "false");
    
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);

    // Close when window quits
    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowDestroyed(WindowEvent e) {
        System.exit(0);
      }
    });

    JComponent comp = Example.createComponent();
    
    NewtHiddenParent hidden = new NewtHiddenParent(window);
    
    try {
      Field f = PopupFactory.class.getDeclaredField("forceHeavyWeightPopupKey");
      f.setAccessible(true);
      comp.putClientProperty(f.get(null), true);
    } catch (IllegalArgumentException ex) {
      ex.printStackTrace();
    } catch (SecurityException ex) {
      ex.printStackTrace();
    } catch (IllegalAccessException ex) {
      ex.printStackTrace();
    } catch (NoSuchFieldException ex) {
      ex.printStackTrace();
    }
    
    RepaintManager.setCurrentManager(new RepaintManager() {
      @Override
      public void addDirtyRegion(Applet applet, int x, int y, int w, int h) {
      }
      
      @Override
      public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
      }
      
      @Override
      public void addDirtyRegion(Window window, int x, int y, int w, int h) {
      }
      
      @Override
      public synchronized void addInvalidComponent(JComponent invalidComponent) {
      }
    });
    
    hidden.setVisible(true);
    hidden.setBounds(0, 0, 500, 500);
    
    // Put into a JRootPane if the component has no Window ancestor
    hidden.add(comp, BorderLayout.CENTER);

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
