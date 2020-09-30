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
package net.opengrabeso.glg2d.impl;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

/**
 * Fulfills the contract of a {@code GraphicsDevice}.
 */
public class GLGraphicsDevice extends GraphicsDevice {
  protected final GLGraphicsConfiguration config;

  public GLGraphicsDevice(GLGraphicsConfiguration config) {
    this.config = config;
  }

  @Override
  public int getType() {
    if (config.isOnScreen()) {
      return TYPE_RASTER_SCREEN;
    } else {
      return TYPE_IMAGE_BUFFER;
    }
  }

  @Override
  public String getIDstring() {
    return "glg2d";
  }

  @Override
  public GraphicsConfiguration[] getConfigurations() {
    return new GraphicsConfiguration[] { getDefaultConfiguration() };
  }

  @Override
  public GraphicsConfiguration getDefaultConfiguration() {
    return config;
  }
}
