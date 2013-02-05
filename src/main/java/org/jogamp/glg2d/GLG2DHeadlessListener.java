/*
 * Copyright 2013 Brandon Borkholder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jogamp.glg2d;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.swing.JComponent;

/**
 * When the component being painting is not part of an existing Swing hierarchy
 * and therefore not receiving standard AWT events, the UI will not function as
 * expected. Reshape events will not be propagated and many features of Swing
 * components will not be available (e.g. indeterminate progress bars).
 * 
 * <p>
 * This will initialize the component as if it was part of a Swing hierarchy on
 * the first call to {@code init(GLDrawable)}. This will listen for reshape
 * events and ensure the component being painted fills the entire canvas. This
 * is useful when painting a Swing component into a NEWT window and the Swing
 * component takes up the entire NEWT frame.
 * </p>
 */
public class GLG2DHeadlessListener implements GLEventListener {
  protected JComponent comp;

  public GLG2DHeadlessListener(JComponent component) {
    if (component == null) {
      throw new NullPointerException("component is null");
    }

    comp = component;
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    comp.addNotify();
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
  }

  @Override
  public void display(GLAutoDrawable drawable) {
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    comp.setSize(width, height);
    comp.validate();
  }
}
