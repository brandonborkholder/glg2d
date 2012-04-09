/**************************************************************************
   Copyright 2012 Brandon Borkholder

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
import java.awt.Composite;
import java.awt.Paint;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

public class G2DGLColorHelper implements G2DDrawingHelper {
  protected GL2 gl;

  protected int canvasHeight;

  protected Color color;

  protected Color background;

  protected Composite composite;

  @Override
  public void setG2D(GLGraphics2D g2d) {
    gl = g2d.getGLContext().getGL().getGL2();
    canvasHeight = g2d.getHeight();
  }

  @Override
  public void push(GLGraphics2D newG2d) {
  }

  @Override
  public void pop(GLGraphics2D parentG2d) {
  }

  @Override
  public void dispose() {
  }

  public void setComposite(Composite comp) {
    gl.glEnable(GL.GL_BLEND);
    if (comp instanceof AlphaComposite) {
      switch (((AlphaComposite) comp).getRule()) {
      /*
       * Since the destination _always_ covers the entire canvas (i.e. there are
       * always color components for every pixel), some of these composites can
       * be collapsed into each other. They matter when Java2D is drawing into
       * an image and the destination may not take up the entire canvas.
       */
      case AlphaComposite.SRC:
      case AlphaComposite.SRC_IN:
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ZERO);
        break;

      case AlphaComposite.SRC_OVER:
      case AlphaComposite.SRC_ATOP:
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        break;

      case AlphaComposite.SRC_OUT:
      case AlphaComposite.CLEAR:
        gl.glBlendFunc(GL.GL_ZERO, GL.GL_ZERO);
        break;

      case AlphaComposite.DST:
      case AlphaComposite.DST_OVER:
        gl.glBlendFunc(GL.GL_ZERO, GL.GL_ONE);
        break;

      case AlphaComposite.DST_IN:
      case AlphaComposite.DST_ATOP:
        gl.glBlendFunc(GL.GL_ZERO, GL.GL_SRC_ALPHA);
        break;

      case AlphaComposite.DST_OUT:
      case AlphaComposite.XOR:
        gl.glBlendFunc(GL.GL_ZERO, GL.GL_ONE_MINUS_SRC_ALPHA);
        break;
      }

      composite = comp;
      // need to pre-multiply the alpha
      setColor(color);
    } else {
      throw new UnsupportedOperationException();
    }
  }

  public Composite getComposite() {
    return composite;
  }

  public void setPaint(Paint paint) {
    if (paint instanceof Color) {
      setColor((Color) paint);
    } else {
      // TODO
      throw new UnsupportedOperationException();
    }
  }

  public Paint getPaint() {
    return color;
  }

  public Color getColor() {
    return color;
  }

  public void setColorNoRespectComposite(Color c) {
    setColor(gl, c, 1);
  }

  public void setColor(Color c) {
    if (c == null) {
      return;
    }

    color = c;
    setColorRespectComposite(c);
  }

  /**
   * Sets the current color with a call to glColor4*. But it respects the
   * AlphaComposite if any. If the AlphaComposite wants to pre-multiply an
   * alpha, pre-multiply it.
   */
  protected void setColorRespectComposite(Color c) {
    float alpha = 1;
    if (composite instanceof AlphaComposite) {
      alpha = ((AlphaComposite) composite).getAlpha();
    }

    setColor(gl, c, alpha);
  }

  private static void setColor(GL2 gl, Color c, float preMultiplyAlpha) {
    int rgb = c.getRGB();
    gl.glColor4ub((byte) (rgb >> 16 & 0xFF), (byte) (rgb >> 8 & 0xFF), (byte) (rgb & 0xFF), (byte) ((rgb >> 24 & 0xFF) * preMultiplyAlpha));
  }

  public void setBackground(Color color) {
    background = color;
    int rgb = background.getRGB();
    gl.glClearColor((rgb >> 16 & 0xFF) / 255F, (rgb >> 8 & 0xFF) / 255F, (rgb & 0xFF) / 255F, (rgb >> 24 & 0xFF) / 255F);
  }

  public Color getBackground() {
    return background;
  }

  public void setPaintMode() {
    // TODO Auto-generated method stub
  }

  public void setXORMode(Color c) {
    // TODO Auto-generated method stub
  }

  public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    // glRasterPos* is transformed, but CopyPixels is not
    int x2 = x + dx;
    int y2 = y + dy + height;
    gl.glRasterPos2i(x2, y2);

    int x1 = x;
    int y1 = canvasHeight - (y + height);
    gl.glCopyPixels(x1, y1, width, height, GL2GL3.GL_COLOR);
  }
}
