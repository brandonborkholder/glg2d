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
package org.jogamp.glg2d.impl;

import java.awt.RenderingHints.Key;
import java.awt.geom.AffineTransform;
import java.util.ArrayDeque;
import java.util.Deque;

import org.jogamp.glg2d.GLG2DTransformHelper;
import org.jogamp.glg2d.GLGraphics2D;

public abstract class AbstractMatrixHelper implements GLG2DTransformHelper {
  protected GLGraphics2D g2d;

  protected Deque<AffineTransform> stack = new ArrayDeque<AffineTransform>();

  @Override
  public void setG2D(GLGraphics2D g2d) {
    this.g2d = g2d;

    stack.clear();
    stack.push(new AffineTransform());
  }

  @Override
  public void push(GLGraphics2D newG2d) {
    stack.push(getTransform());
  }

  @Override
  public void pop(GLGraphics2D parentG2d) {
    stack.pop();
    flushTransformToOpenGL();
  }

  @Override
  public void setHint(Key key, Object value) {
    // nop
  }

  @Override
  public void resetHints() {
    // nop
  }

  @Override
  public void dispose() {
    // nop
  }

  @Override
  public void translate(int x, int y) {
    translate((double) x, (double) y);
    flushTransformToOpenGL();
  }

  @Override
  public void translate(double tx, double ty) {
    getTransform0().translate(tx, ty);
    flushTransformToOpenGL();
  }

  @Override
  public void rotate(double theta) {
    getTransform0().rotate(theta);
    flushTransformToOpenGL();
  }

  @Override
  public void rotate(double theta, double x, double y) {
    getTransform0().rotate(theta, x, y);
    flushTransformToOpenGL();
  }

  @Override
  public void scale(double sx, double sy) {
    getTransform0().scale(sx, sy);
    flushTransformToOpenGL();
  }

  @Override
  public void shear(double shx, double shy) {
    getTransform0().shear(shx, shy);
    flushTransformToOpenGL();
  }

  @Override
  public void transform(AffineTransform Tx) {
    getTransform0().concatenate(Tx);
    flushTransformToOpenGL();
  }

  @Override
  public void setTransform(AffineTransform transform) {
    getTransform0().setTransform(transform);
    flushTransformToOpenGL();
  }

  @Override
  public AffineTransform getTransform() {
    return (AffineTransform) getTransform0().clone();
  }

  /**
   * Returns the {@code AffineTransform} at the top of the stack, <em>not</em> a
   * copy.
   */
  protected AffineTransform getTransform0() {
    return stack.peek();
  }

  /**
   * Sends the {@code AffineTransform} that's on top of the stack to the video
   * card.
   */
  protected abstract void flushTransformToOpenGL();
}
