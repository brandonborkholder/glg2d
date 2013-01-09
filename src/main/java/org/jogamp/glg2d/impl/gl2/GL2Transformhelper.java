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
package org.jogamp.glg2d.impl.gl2;


import java.awt.RenderingHints.Key;
import java.awt.geom.AffineTransform;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.jogamp.glg2d.GLG2DTransformHelper;
import org.jogamp.glg2d.GLG2DUtils;
import org.jogamp.glg2d.GLGraphics2D;

public class GL2Transformhelper implements GLG2DTransformHelper {
  protected static final float RAD_TO_DEG = 180f / (float) Math.PI;

  protected GLGraphics2D g2d;

  protected GL2 gl;

  @Override
  public void setG2D(GLGraphics2D g2d) {
    this.g2d = g2d;
    gl = g2d.getGLContext().getGL().getGL2();

    setupGLView();
  }

  @Override
  public void push(GLGraphics2D newG2d) {
    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glPushMatrix();
  }

  @Override
  public void pop(GLGraphics2D parentG2d) {
    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glPopMatrix();
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
  }

  protected void setupGLView() {
    int[] viewportDimensions = new int[4];
    gl.glGetIntegerv(GL.GL_VIEWPORT, viewportDimensions, 0);
    int width = viewportDimensions[2];
    int height = viewportDimensions[3];

    // setup projection
    gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
    gl.glLoadIdentity();
    gl.glOrtho(0, width, 0, height, -1, 1);

    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glLoadIdentity();

    // do the transform from Graphics2D coords to openGL coords
    gl.glTranslatef(0, height, 0);
    gl.glScalef(1, -1, 1);

    gl.glMatrixMode(GL.GL_TEXTURE);
    gl.glLoadIdentity();
  }

  @Override
  public void translate(int x, int y) {
    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glTranslatef(x, y, 0);
  }

  @Override
  public void translate(double tx, double ty) {
    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glTranslatef((float) tx, (float) ty, 0);
  }

  @Override
  public void rotate(double theta) {
    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glRotatef((float) theta * RAD_TO_DEG, 0, 0, 1);
  }

  @Override
  public void rotate(double theta, double x, double y) {
    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glTranslatef((float) x, (float) y, 0);
    gl.glRotated(theta * RAD_TO_DEG, 0, 0, 1);
    gl.glTranslatef((float) -x, (float) -y, 0);
  }

  @Override
  public void scale(double sx, double sy) {
    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glScaled(sx, sy, 1);
  }

  @Override
  public void shear(double shx, double shy) {
    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    float[] shear = new float[] {
        1, (float) shy, 0, 0,
        (float) shx, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1 };
    gl.glMultMatrixf(shear, 0);
  }

  @Override
  public void transform(AffineTransform Tx) {
    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    GLG2DUtils.multMatrix(gl, Tx);
  }

  @Override
  public void setTransform(AffineTransform transform) {
    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glLoadIdentity();
    gl.glTranslatef(0, g2d.getCanvasHeight(), 0);
    gl.glScalef(1, -1, 1);
    GLG2DUtils.multMatrix(gl, transform);
  }

  @Override
  public AffineTransform getTransform() {
    return GLG2DUtils.getModelTransform(gl, g2d.getCanvasHeight());
  }
}
