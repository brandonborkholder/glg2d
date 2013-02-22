package org.jogamp.glg2d.newt;

import java.awt.HeadlessException;
import java.awt.Window;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class NewtHiddenParent extends Window {
  com.jogamp.newt.Window newtWindow;

  public NewtHiddenParent(com.jogamp.newt.Window window) throws HeadlessException {
    super(null);
    newtWindow = window;
    setVisible(true);
  }

  @Override
  public boolean isShowing() {
    return true;
  }

  public void setContent(JComponent comp) {
    removeAll();
    add(comp);
  }
}
