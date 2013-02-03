package org.jogamp.glg2d.event;

import java.awt.Component;
import java.awt.Point;

import com.jogamp.newt.event.InputEvent;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;

public class NewtMouseEventTranslator extends MouseEventTranslator implements MouseListener {
  public NewtMouseEventTranslator(Component target) {
    super(target);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    translateMouseEvent(e);
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    translateMouseEvent(e);
  }

  @Override
  public void mouseExited(MouseEvent e) {
    translateMouseEvent(e);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    translateMouseEvent(e);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    translateMouseEvent(e);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    translateMouseEvent(e);
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    translateMouseEvent(e);
  }

  @Override
  public void mouseWheelMoved(MouseEvent e) {
    translateMouseWheelEvent(e);
  }
  
  protected void translateMouseWheelEvent(MouseEvent e) {
    int id = newtType2Awt(e.getEventType());
    int modifiers = newtModifiers2Awt(e.getModifiers());
    long when = e.getWhen();
    int wheelRotation = -e.getWheelRotation();
    publishMouseWheelEvent(id, when, modifiers, wheelRotation, new Point(e.getX(), e.getY()));
  }

  protected void translateMouseEvent(MouseEvent e) {
    int button = newtButton2Awt(e.getButton());
    int id = newtType2Awt(e.getEventType());
    int modifiers = newtModifiers2Awt(e.getModifiers());
    long when = e.getWhen();
    int clickCount = e.getClickCount();

    publishMouseEvent(id, when, modifiers, clickCount, button, new Point(e.getX(), e.getY()));
  }

  public static int newtModifiers2Awt(int newtMods) {
    int awtMods = 0;

    if ((newtMods & InputEvent.SHIFT_MASK) != 0) {
      awtMods |= java.awt.event.InputEvent.SHIFT_MASK;
    }

    if ((newtMods & InputEvent.CTRL_MASK) != 0) {
      awtMods |= java.awt.event.InputEvent.CTRL_MASK;
    }

    if ((newtMods & InputEvent.META_MASK) != 0) {
      awtMods |= java.awt.event.InputEvent.META_MASK;
    }

    if ((newtMods & InputEvent.ALT_MASK) != 0) {
      awtMods |= java.awt.event.InputEvent.ALT_MASK;
    }

    if ((newtMods & InputEvent.ALT_GRAPH_MASK) != 0) {
      awtMods |= java.awt.event.InputEvent.ALT_GRAPH_MASK;
    }

    return awtMods;
  }

  public static int newtButton2Awt(int newtButton) {
    switch (newtButton) {
    case MouseEvent.BUTTON1:
      return java.awt.event.MouseEvent.BUTTON1;
    case MouseEvent.BUTTON2:
      return java.awt.event.MouseEvent.BUTTON2;
    case MouseEvent.BUTTON3:
      return java.awt.event.MouseEvent.BUTTON3;
    default:
      return 0;
    }
  }

  public static int newtType2Awt(int newtMouseType) {
    switch (newtMouseType) {
    case MouseEvent.EVENT_MOUSE_CLICKED:
      return java.awt.event.MouseEvent.MOUSE_CLICKED;
    case MouseEvent.EVENT_MOUSE_ENTERED:
      return java.awt.event.MouseEvent.MOUSE_ENTERED;
    case MouseEvent.EVENT_MOUSE_EXITED:
      return java.awt.event.MouseEvent.MOUSE_EXITED;
    case MouseEvent.EVENT_MOUSE_MOVED:
      return java.awt.event.MouseEvent.MOUSE_MOVED;
    case MouseEvent.EVENT_MOUSE_PRESSED:
      return java.awt.event.MouseEvent.MOUSE_PRESSED;
    case MouseEvent.EVENT_MOUSE_RELEASED:
      return java.awt.event.MouseEvent.MOUSE_RELEASED;
    case MouseEvent.EVENT_MOUSE_DRAGGED:
      return java.awt.event.MouseEvent.MOUSE_DRAGGED;
    case MouseEvent.EVENT_MOUSE_WHEEL_MOVED:
      return java.awt.event.MouseEvent.MOUSE_WHEEL;
    default:
      return 0;
    }
  }
}
