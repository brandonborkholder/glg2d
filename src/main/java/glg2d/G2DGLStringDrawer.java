/**************************************************************************
   Copyright 2011 Brandon Borkholder

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

package glg2d;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Draws text for the {@code GLGraphics2D} class.
 */
public class G2DGLStringDrawer implements G2DDrawingHelper {
  protected Map<Font, TextRenderer> cache = new HashMap<Font, TextRenderer>();

  protected Font font;

  protected Deque<Font> fontStack = new ArrayDeque<Font>(10);

  protected GLGraphics2D g2d;

  protected boolean antiAlias;

  @Override
  public void setG2D(GLGraphics2D g2d) {
    this.g2d = g2d;
  }

  @Override
  public void push(GLGraphics2D newG2d) {
    fontStack.push(font);
  }

  @Override
  public void pop(GLGraphics2D parentG2d) {
    setAntiAlias(parentG2d.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
    if (!fontStack.isEmpty()) {
      font = fontStack.pop();
    }
  }

  @Override
  public void dispose() {
    resetCache();
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

  public void setAntiAlias(Object value) {
    boolean doAlias = true;
    if (value == null || value == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF || value == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT) {
      doAlias = false;
    }
    if (antiAlias == doAlias) {
      return;
    }

    antiAlias = doAlias;
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

  /**
   * Sets the font color, respecting the AlphaComposite if it wants to
   * pre-multiply an alpha.
   */
  protected void setTextColorRespectComposite(TextRenderer renderer, Color color) {
    if (g2d.getComposite() instanceof AlphaComposite) {
      float alpha = ((AlphaComposite) g2d.getComposite()).getAlpha();
      if (alpha < 1) {
        float[] rgba = color.getRGBComponents(null);
        color = new Color(rgba[0], rgba[1], rgba[2], alpha * rgba[3]);
      }
    }

    renderer.setColor(color);
  }

  public void drawString(String string, Color color, int x, int y) {
    TextRenderer renderer = getRenderer(getFont());

    begin(renderer, color);
    renderer.draw3D(string, x, g2d.getHeight() - y, 0, 1);
    end(renderer);
  }

  protected void begin(TextRenderer renderer, Color textColor) {
    setTextColorRespectComposite(renderer, textColor);

    GL gl = g2d.getGLContext().getGL();
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glPushMatrix();
    gl.glScalef(1, -1, 1);
    gl.glTranslatef(0, -g2d.getHeight(), 0);

    renderer.begin3DRendering();
  }

  protected void end(TextRenderer renderer) {
    renderer.end3DRendering();

    GL gl = g2d.getGLContext().getGL();
    gl.glPopMatrix();
  }

  protected void resetCache() {
    for (TextRenderer renderer : cache.values()) {
      renderer.dispose();
    }

    cache.clear();
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
      fontRenderContext = frc;
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
