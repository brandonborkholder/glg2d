/*
 * Copyright 2012 Brandon Borkholder
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

import java.awt.Rectangle;
import java.util.Map;
import java.util.Map.Entry;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.JComponent;
import javax.swing.RepaintManager;

/**
 * Helps wrap the {@code GLGraphics2D} object within the JOGL framework.
 */
public class GLG2DEventListener extends GLG2DSimpleEventListener {
  Map<JComponent, Rectangle> repaints;

  GLG2DPanel canvas;

  /**
   * Creates a new listener that will paint using the {@code baseComponent} on
   * each call to {@link #display(GLAutoDrawable)}. The provided
   * {@code baseComponent} is used to provide default font, backgroundColor,
   * etc. to the {@code GLGraphics2D} object. It is also used for width, height
   * of the viewport in OpenGL.
   */
  public GLG2DEventListener(JComponent baseComponent) {
    super(baseComponent);
  }

  /**
   * Paints using the {@code GLGraphics2D} object. This could be forwarded to
   * any code that expects to draw using the Java2D framework.
   * <p>
   * Currently is paints the component provided, turning off double-buffering in
   * the {@code RepaintManager} to force drawing directly to the
   * {@code Graphics2D} object.
   * </p>
   */
  @Override
  protected void paintGL(GLGraphics2D g2d) {
    RepaintManager mgr = RepaintManager.currentManager(comp);
    boolean doubleBuffer = mgr.isDoubleBufferingEnabled();
    mgr.setDoubleBufferingEnabled(false);

    canvas.g2d = g2d;

    if (isPaintingDirtyRects()) {
      paintDirtyRects();
    } else {
      comp.paint(g2d);
    }

    canvas.g2d = null;

    mgr.setDoubleBufferingEnabled(doubleBuffer);
  }

  protected boolean isPaintingDirtyRects() {
    return repaints != null;
  }

  protected void paintDirtyRects() {
    for (Entry<JComponent, Rectangle> entry : repaints.entrySet()) {
      entry.getKey().paintImmediately(entry.getValue());
    }

    repaints = null;
  }
}
