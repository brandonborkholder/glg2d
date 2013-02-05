package org.jogamp.glg2d.event;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class AWTMouseEventTranslator extends MouseEventTranslator implements MouseListener, MouseMotionListener, MouseWheelListener {
  public AWTMouseEventTranslator(Component target) {
    super(target);
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    translateMouseWheelEvent(e);
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    translateMouseEvent(e);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    translateMouseEvent(e);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
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
  public void mouseEntered(MouseEvent e) {
    translateMouseEvent(e);
  }

  @Override
  public void mouseExited(MouseEvent e) {
    translateMouseEvent(e);
  }

  protected void translateMouseEvent(MouseEvent e) {
    publishMouseEvent(e.getID(), e.getWhen(), e.getModifiers(), e.getClickCount(), e.getButton(), e.getPoint());
  }
  
  protected void translateMouseWheelEvent(MouseWheelEvent e) {
    publishMouseWheelEvent(e.getID(), e.getWhen(), e.getModifiers(), e.getWheelRotation(), e.getPoint());
  }
}
