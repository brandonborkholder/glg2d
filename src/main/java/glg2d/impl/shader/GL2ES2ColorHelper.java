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
package glg2d.impl.shader;

import glg2d.impl.AbstractColorHelper;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;

public class GL2ES2ColorHelper extends AbstractColorHelper {
  protected float[] foregroundRGBA = new float[4];

  @Override
  public void setColorNoRespectComposite(Color c) {
    foregroundRGBA[0] = c.getRed() / 255f;
    foregroundRGBA[1] = c.getGreen() / 255f;
    foregroundRGBA[2] = c.getBlue() / 255f;
    foregroundRGBA[3] = c.getAlpha() / 255f;
  }

  @Override
  public void setColorRespectComposite(Color c) {
    float alpha = getCompositeAlpha();
    foregroundRGBA[0] = c.getRed() / 255f;
    foregroundRGBA[1] = c.getGreen() / 255f;
    foregroundRGBA[2] = c.getBlue() / 255f;
    foregroundRGBA[3] = (c.getAlpha() / 255f) * alpha;
  }

  @Override
  public void setPaintMode() {
    // not implemented yet
  }

  @Override
  public void setXORMode(Color c) {
    // not implemented yet
  }

  @Override
  public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    // not implemented yet
  }

  public float[] getForegroundRGBA() {
    return foregroundRGBA;
  }

  public float getCompositeAlpha() {
    Composite composite = getComposite();
    float alpha = 1;
    if (composite instanceof AlphaComposite) {
      alpha = ((AlphaComposite) composite).getAlpha();
    }

    return alpha;
  }
}
