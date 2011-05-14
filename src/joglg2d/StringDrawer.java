/**************************************************************************
   Copyright 2010 Brandon Borkholder

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 ***************************************************************************/

package joglg2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;

public class StringDrawer {
  protected Map<Font, TextRenderer> cache = new HashMap<Font, TextRenderer>();

  protected Font font;

  protected JOGLG2D g2d;

  protected boolean antiAlias;

  public StringDrawer(JOGLG2D g2d) {
    this.g2d = g2d;
  }

  public void setFont(Font font) {
    this.font = font;
  }

  public Font getFont() {
    return font;
  }

  public FontMetrics getFontMetrics(Font font) {
    return new GLFontMetrics(font, getFontRenderContext(), getRenderer(font));
  }

  public FontRenderContext getFontRenderContext() {
    return new FontRenderContext(g2d.getTransform(), true, false);
  }

  public void setAntiAlias(boolean alias) {
    if (antiAlias == alias) {
      return;
    }

    antiAlias = alias;
    resetCache();
  }

  public void drawString(AttributedCharacterIterator iterator, int x, int y) {
  }

  public void drawString(AttributedCharacterIterator iterator, float x, float y) {
  }

  public void drawString(String string, Color color, float x, float y) {
    drawString(string, color, (int) x, (int) y);
  }

  protected TextRenderer getRenderer(Font font) {
    TextRenderer renderer = cache.get(font);
    if (renderer == null) {
      renderer = new TextRenderer(font, antiAlias, false);
      cache.put(font, renderer);
    }

    return renderer;
  }

  public void drawString(String string, Color color, int x, int y) {
    TextRenderer renderer = getRenderer(getFont());
    renderer.setColor(color);

    GL gl = g2d.getGL();
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glPushMatrix();
    gl.glScalef(1, -1, 1);
    gl.glTranslatef(0, -g2d.getHeight(), 0);

    renderer.begin3DRendering();
    renderer.draw3D(string, x, g2d.getHeight() - y, 0, 1);
    renderer.end3DRendering();

    gl.glPopMatrix();
  }

  protected void resetCache() {
    for (TextRenderer renderer : cache.values()) {
      renderer.dispose();
    }

    cache.clear();
  }

  public void dispose() {
    resetCache();
  }

  /**
   * The default implementation is good enough for now.
   */
  public static class GLFontMetrics extends FontMetrics {
    private static final long serialVersionUID = 3676850359220061793L;

    protected TextRenderer renderer;

    protected FontRenderContext fontRenderContext;

    protected int[] cachedWidths = new int[255];

    public GLFontMetrics(Font font, FontRenderContext frc, TextRenderer renderer) {
      super(font);
      this.renderer = renderer;
      this.fontRenderContext = frc;
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
        if (width == 0) {
          width = (int) renderer.getCharWidth(ch);
          cachedWidths[ch] = width;
        }
      } else {
        width = (int) renderer.getCharWidth(ch);
      }

      return width;
    }
  }
}
