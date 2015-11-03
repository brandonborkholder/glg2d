/*
 * Copyright 2015 Brandon Borkholder
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

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;

import com.jogamp.opengl.GLDrawable;

/**
 * Fulfills the contract of a {@code GraphicsConfiguration}.
 *
 * <p>
 * Implementation note: this object is intended primarily to allow callers to
 * create compatible images. The transforms and bounds should be thought out
 * before being used.
 * </p>
 */
public class GLGraphicsConfiguration extends GraphicsConfiguration {
  private final GLDrawable target;

  private final GLGraphicsDevice device;

  public GLGraphicsConfiguration(GLDrawable drawable) {
    target = drawable;
    device = new GLGraphicsDevice(this);
  }

  @Override
  public GraphicsDevice getDevice() {
    return device;
  }

  @Override
  public BufferedImage createCompatibleImage(int width, int height) {
    return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
  }

  /*
   * Any reasonable {@code ColorModel} can be transformed into a texture we can
   * render in OpenGL. I'm not worried about creating an exactly correct one
   * right now.
   */
  @Override
  public ColorModel getColorModel() {
    return ColorModel.getRGBdefault();
  }

  @Override
  public ColorModel getColorModel(int transparency) {
    switch (transparency) {
    case Transparency.OPAQUE:
    case Transparency.TRANSLUCENT:
      return getColorModel();
    case Transparency.BITMASK:
      return new DirectColorModel(25, 0xff0000, 0xff00, 0xff, 0x1000000);
    default:
      return null;
    }
  }

  @Override
  public AffineTransform getDefaultTransform() {
    return new AffineTransform();
  }

  @Override
  public AffineTransform getNormalizingTransform() {
    return new AffineTransform();
  }

  @Override
  public Rectangle getBounds() {
    return new Rectangle(target.getSurfaceWidth(), target.getSurfaceHeight());
  }

  public GLDrawable getTarget() {
    return target;
  }
}
