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
package glg2d.impl.shader.text;

import glg2d.GLG2DTextHelper;
import glg2d.GLGraphics2D;
import glg2d.impl.AbstractTextDrawer;
import glg2d.impl.shader.GLShaderGraphics2D;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;

import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import com.jogamp.opengl.util.awt.TextRenderer;

public class GL2ES2TextDrawer extends AbstractTextDrawer {
  protected FontRenderCache cache = new FontRenderCache();

  protected GLShaderGraphics2D g2d;

  @Override
  public void setG2D(GLGraphics2D g2d) {
    if (g2d instanceof GLShaderGraphics2D) {
      this.g2d = (GLShaderGraphics2D) g2d;
    } else {
      throw new IllegalArgumentException(GLGraphics2D.class.getName() + " implementation must be instance of "
          + GLShaderGraphics2D.class.getSimpleName());
    }

    super.setG2D(g2d);
  }

  @Override
  public void dispose() {
    cache.dispose();
  }

  @Override
  public void drawString(String string, int x, int y) {
    drawString(string, (float) x, (float) y);
  }

  @Override
  public void drawString(AttributedCharacterIterator iterator, int x, int y) {
    drawString(iterator, (float) x, (float) y);
  }

  @Override
  public void drawString(AttributedCharacterIterator iterator, float x, float y) {
    drawChars(iterator, x, y);
  }

  @Override
  public void drawString(String string, float x, float y) {
    drawChars(new StringCharacterIterator(string), x, y);
  }

  protected void drawChars(CharacterIterator textItr, float x, float y) {
    GlyphVector glyphs = getFont().createGlyphVector(getFontRenderContext(), textItr);
    
    for (int i = 0; i < glyphs.getNumGlyphs(); i++) {
      Shape s = glyphs.getOutline();
    }
  }

  @SuppressWarnings("serial")
  public static class FontRenderCache extends HashMap<Font, TextRenderer[]> {
    public TextRenderer getRenderer(Font font, boolean antiAlias) {
      TextRenderer[] renderers = get(font);
      if (renderers == null) {
        renderers = new TextRenderer[2];
        put(font, renderers);
      }

      TextRenderer renderer = renderers[antiAlias ? 1 : 0];

      if (renderer == null) {
        renderer = new TextRenderer(font, antiAlias, false);
        renderers[antiAlias ? 1 : 0] = renderer;
      }

      return renderer;
    }

    public void dispose() {
      for (TextRenderer[] value : values()) {
        if (value[0] != null) {
          value[0].dispose();
        }
        if (value[1] != null) {
          value[1].dispose();
        }
      }
    }
  }
}
