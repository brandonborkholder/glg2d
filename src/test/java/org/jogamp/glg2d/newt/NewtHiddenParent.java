package org.jogamp.glg2d.newt;

import java.awt.HeadlessException;
import java.awt.Window;

@SuppressWarnings("serial")
public class NewtHiddenParent extends Window {
  com.jogamp.newt.Window newtWindow;

  public NewtHiddenParent(com.jogamp.newt.Window window) throws HeadlessException {
    super(null);
    newtWindow = window;
  }

  @Override
  public boolean isShowing() {
    return true;
  }
}
