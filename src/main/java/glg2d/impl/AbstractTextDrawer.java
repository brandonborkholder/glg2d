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
package glg2d.impl;

import glg2d.GLG2DTextHelper;
import glg2d.GLGraphics2D;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public abstract class AbstractTextDrawer implements GLG2DTextHelper {
  protected GLGraphics2D g2d;

  protected Deque<FontState> stack = new ArrayDeque<FontState>();

  @Override
  public void setG2D(GLGraphics2D g2d) {
    this.g2d = g2d;

    stack.clear();
    stack.push(new FontState());
  }

  @Override
  public void push(GLGraphics2D newG2d) {
    stack.push(stack.peek().clone());
  }

  @Override
  public void pop(GLGraphics2D parentG2d) {
    stack.pop();
  }

  @Override
  public void setHint(Key key, Object value) {
    if (key == RenderingHints.KEY_TEXT_ANTIALIASING) {
      stack.peek().antiAlias = value == RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
    }
  }

  @Override
  public void resetHints() {
    setHint(RenderingHints.KEY_TEXT_ANTIALIASING, null);
  }

  @Override
  public void setFont(Font font) {
    stack.peek().font = font;
  }

  @Override
  public Font getFont() {
    return stack.peek().font;
  }

  @Override
  public FontMetrics getFontMetrics(Font font) {
    return new GLFontMetrics(font, getFontRenderContext());
  }

  @Override
  public FontRenderContext getFontRenderContext() {
    return new FontRenderContext(g2d.getTransform(), stack.peek().antiAlias, false);
  }

  /**
   * The default implementation is good enough for now.
   */
  public static class GLFontMetrics extends FontMetrics {
    private static final long serialVersionUID = 3676850359220061793L;

    protected FontRenderContext fontRenderContext;

    protected int[] cachedWidths = new int[255];

    public GLFontMetrics(Font font, FontRenderContext frc) {
      super(font);
      fontRenderContext = frc;
      Arrays.fill(cachedWidths, -1);
    }

    @Override
    public FontRenderContext getFontRenderContext() {
      return fontRenderContext;
    }

    @Override
    public int charsWidth(char[] data, int off, int len) {
      if (len <= 0) {
        return 0;
      }

      if (font.hasLayoutAttributes()) {
        String str = new String(data, off, len);
        return (int) new TextLayout(str, font, getFontRenderContext()).getAdvance();
      } else {
        int width = 0;
        for (int i = 0; i < len; i++) {
          width += charWidth(data[off + i]);
        }

        return width;
      }
    }

    @Override
    public int charWidth(char ch) {
      int width;
      if (ch < 255) {
        width = cachedWidths[ch];
        if (width < 0) {
          width = (int) getFont().getStringBounds(new char[] { ch }, 0, 1, getFontRenderContext()).getWidth();
          cachedWidths[ch] = width;
        }
      } else {
        width = (int) getFont().getStringBounds(new char[] { ch }, 0, 1, getFontRenderContext()).getWidth();
      }

      return width;
    }
  }

  protected static class FontState implements Cloneable {
    public Font font;
    public boolean antiAlias;

    @Override
    public FontState clone() {
      try {
        return (FontState) super.clone();
      } catch (CloneNotSupportedException e) {
        // can't think of a reason this would happen
        throw new RuntimeException(e);
      }
    }
  }
}
