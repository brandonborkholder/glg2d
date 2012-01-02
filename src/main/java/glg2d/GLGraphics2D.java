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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;
import javax.media.opengl.Threading;

/**
 * Implements the standard {@code Graphics2D} functionality, but instead draws
 * to an OpenGL canvas.
 */
public class GLGraphics2D extends Graphics2D implements Cloneable {
  protected static final double RAD_TO_DEG = 180d / Math.PI;

  protected GLGraphics2D parent;

  protected GLContext glContext;

  protected GL gl;

  protected int height;

  protected int width;

  protected boolean isDisposed;

  protected Color color;

  protected Color background;

  protected G2DGLShapeDrawer shapeDrawer;

  protected G2DGLImageDrawer imageDrawer;

  protected G2DGLStringDrawer stringDrawer;

  protected Rectangle clip;

  protected Composite composite;

  protected GraphicsConfiguration graphicsConfig;

  protected RenderingHints hints;

  protected G2DDrawingHelper[] helpers = new G2DDrawingHelper[0];

  public GLGraphics2D(int width, int height) {
    this.height = height;
    this.width = width;

    hints = new RenderingHints(Collections.<Key, Object> emptyMap());

    createDrawingHelpers();
  }

  protected void createDrawingHelpers() {
    shapeDrawer = new G2DGLShapeDrawer();
    imageDrawer = new G2DGLImageDrawer();
    stringDrawer = new G2DGLStringDrawer();

    addG2DDrawingHelper(shapeDrawer);
    addG2DDrawingHelper(imageDrawer);
    addG2DDrawingHelper(stringDrawer);
  }

  public void addG2DDrawingHelper(G2DDrawingHelper helper) {
    helpers = Arrays.copyOf(helpers, helpers.length + 1);
    helpers[helpers.length - 1] = helper;
  }

  public void removeG2DDrawingHelper(G2DDrawingHelper helper) {
    for (int i = 0; i < helpers.length; i++) {
      if (helpers[i] == helper) {
        System.arraycopy(helpers, i + 1, helpers, i, helpers.length - (i + 1));
        helpers = Arrays.copyOf(helpers, helpers.length - 1);
        break;
      }
    }
  }

  protected void setCanvas(GLAutoDrawable drawable) {
    glContext = drawable.getContext();
    gl = glContext.getGL();

    for (G2DDrawingHelper helper : helpers) {
      helper.setG2D(this);
    }
  }

  protected void prePaint(Component component) {
    // push all GL states
    gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
    gl.glPushClientAttrib((int) GL.GL_ALL_CLIENT_ATTRIB_BITS);

    // set the GL state to use defaults from the component
    setBackground(component.getBackground());
    setColor(component.getForeground());
    setFont(component.getFont());
    setStroke(new BasicStroke());
    setComposite(AlphaComposite.SrcOver);
    setClip(null);
    setRenderingHints(null);
    graphicsConfig = component.getGraphicsConfiguration();

    // now enable some flags we'll use
    gl.glDisable(GL.GL_ALPHA_TEST);
    gl.glDisable(GL.GL_DEPTH_TEST);
    gl.glDisable(GL.GL_CULL_FACE);
    gl.glShadeModel(GL.GL_FLAT);
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
  }

  protected void postPaint() {
    gl.glPopClientAttrib();
    gl.glPopAttrib();
    gl.glFlush();
  }

  public GLContext getGLContext() {
    return glContext;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  protected void glDispose() {
    for (G2DDrawingHelper helper : helpers) {
      helper.dispose();
    }
  }

  @Override
  public void draw(Shape s) {
    shapeDrawer.draw(s);
  }

  @Override
  public void drawString(String str, int x, int y) {
    stringDrawer.drawString(str, color, x, y);
  }

  @Override
  public void drawString(String str, float x, float y) {
    stringDrawer.drawString(str, color, x, y);
  }

  @Override
  public void drawString(AttributedCharacterIterator iterator, int x, int y) {
    stringDrawer.drawString(iterator, x, y);
  }

  @Override
  public void drawString(AttributedCharacterIterator iterator, float x, float y) {
    stringDrawer.drawString(iterator, x, y);
  }

  @Override
  public void drawGlyphVector(GlyphVector g, float x, float y) {
    shapeDrawer.fill(g.getOutline(x, y));
  }

  @Override
  public void fill(Shape s) {
    shapeDrawer.fill(s);
  }

  @Override
  public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
    if (clip != null) {
      rect = clip.intersection(rect);
    }

    if (rect.isEmpty()) {
      return false;
    }

    if (onStroke) {
      s = shapeDrawer.getStroke().createStrokedShape(s);
    }

    s = getTransform().createTransformedShape(s);
    return s.intersects(rect);
  }

  @Override
  public GraphicsConfiguration getDeviceConfiguration() {
    return graphicsConfig;
  }

  @Override
  public Composite getComposite() {
    return composite;
  }

  @Override
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

  @Override
  public void setPaint(Paint paint) {
    if (paint instanceof Color) {
      setColor((Color) paint);
    } else {
      // TODO
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public void setRenderingHint(Key hintKey, Object hintValue) {
    if (!hintKey.isCompatibleValue(hintValue)) {
      throw new IllegalArgumentException(hintValue + " is not compatible with " + hintKey);
    } else if (hintKey == RenderingHints.KEY_TEXT_ANTIALIASING) {
      stringDrawer.setAntiAlias(hintValue);
    } else if (hintKey == RenderingHints.KEY_ANTIALIASING) {
      shapeDrawer.setAntiAlias(hintValue);
    }
  }

  @Override
  public Object getRenderingHint(Key hintKey) {
    return hints.get(hintKey);
  }

  @Override
  public void setRenderingHints(Map<?, ?> hints) {
    resetRenderingHints();
    if (hints != null) {
      addRenderingHints(hints);
    }
  }

  protected void resetRenderingHints() {
    hints = new RenderingHints(Collections.<Key, Object> emptyMap());
    stringDrawer.setAntiAlias(RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
    shapeDrawer.setAntiAlias(RenderingHints.VALUE_ANTIALIAS_DEFAULT);
  }

  @Override
  public void addRenderingHints(Map<?, ?> hints) {
    for (Entry<?, ?> entry : hints.entrySet()) {
      if (entry.getKey() instanceof Key) {
        setRenderingHint((Key) entry.getKey(), entry.getValue());
      }
    }
  }

  @Override
  public RenderingHints getRenderingHints() {
    return (RenderingHints) hints.clone();
  }

  @Override
  public void translate(int x, int y) {
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glTranslatef(x, y, 0);
  }

  @Override
  public void translate(double tx, double ty) {
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glTranslated(tx, ty, 0);
  }

  @Override
  public void rotate(double theta) {
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glRotated(theta * RAD_TO_DEG, 0, 0, 1);
  }

  @Override
  public void rotate(double theta, double x, double y) {
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glTranslated(x, y, 0);
    gl.glRotated(theta * RAD_TO_DEG, 0, 0, 1);
    gl.glTranslated(-x, -y, 0);
  }

  @Override
  public void scale(double sx, double sy) {
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glScaled(sx, sy, 1);
  }

  @Override
  public void shear(double shx, double shy) {
    gl.glMatrixMode(GL.GL_MODELVIEW);
    double[] shear = new double[] {
        1, shy, 0, 0,
        shx, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1 };
    gl.glMultMatrixd(shear, 0);
  }

  @Override
  public void transform(AffineTransform Tx) {
    gl.glMatrixMode(GL.GL_MODELVIEW);
    multMatrix(gl, Tx);
  }

  @Override
  public void setTransform(AffineTransform transform) {
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glLoadIdentity();
    gl.glTranslatef(0, height, 0);
    gl.glScalef(1, -1, 1);
    multMatrix(gl, transform);
  }

  public static void multMatrix(GL gl, AffineTransform transform) {
    double[] matrix = new double[16];
    matrix[0] = transform.getScaleX();
    matrix[1] = transform.getShearY();
    matrix[4] = transform.getShearX();
    matrix[5] = transform.getScaleY();
    matrix[10] = 1;
    matrix[12] = transform.getTranslateX();
    matrix[13] = transform.getTranslateY();
    matrix[15] = 1;

    gl.glMultMatrixd(matrix, 0);
  }

  @Override
  public AffineTransform getTransform() {
    double[] m = new double[16];
    gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, m, 0);

    /*
     * Since the MODELVIEW matrix includes the transform from Java2D to OpenGL
     * coords, we remove that transform inline here.
     */
    return new AffineTransform(m[0], -m[1], m[4], -m[5], m[12], height - m[13]);
  }

  @Override
  public Paint getPaint() {
    return color;
  }

  @Override
  public Color getColor() {
    return color;
  }

  @Override
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

  public static void setColor(GL gl, Color c, float preMultiplyAlpha) {
    int rgb = c.getRGB();
    gl.glColor4ub((byte) (rgb >> 16 & 0xFF), (byte) (rgb >> 8 & 0xFF), (byte) (rgb & 0xFF), (byte) ((rgb >> 24 & 0xFF) * preMultiplyAlpha));
  }

  @Override
  public void setBackground(Color color) {
    background = color;
    int rgb = background.getRGB();
    gl.glClearColor((rgb >> 16 & 0xFF) / 255F, (rgb >> 8 & 0xFF) / 255F, (rgb & 0xFF) / 255F, (rgb >> 24 & 0xFF) / 255F);
  }

  @Override
  public Color getBackground() {
    return background;
  }

  @Override
  public Stroke getStroke() {
    return shapeDrawer.getStroke();
  }

  @Override
  public void setStroke(Stroke s) {
    shapeDrawer.setStroke(s);
  }

  @Override
  public void setPaintMode() {
    // TODO Auto-generated method stub

  }

  @Override
  public void setXORMode(Color c1) {
    // TODO Auto-generated method stub

  }

  @Override
  public Font getFont() {
    return stringDrawer.getFont();
  }

  @Override
  public void setFont(Font font) {
    stringDrawer.setFont(font);
  }

  @Override
  public FontMetrics getFontMetrics(Font f) {
    return stringDrawer.getFontMetrics(f);
  }

  @Override
  public FontRenderContext getFontRenderContext() {
    return stringDrawer.getFontRenderContext();
  }

  @Override
  public Rectangle getClipBounds() {
    if (clip == null) {
      return null;
    } else {
      try {
        return getTransform().createInverse().createTransformedShape(clip).getBounds();
      } catch (NoninvertibleTransformException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void clip(Shape s) {
    setClip(s.getBounds(), true);
  }

  @Override
  public void clipRect(int x, int y, int width, int height) {
    setClip(new Rectangle(x, y, width, height), true);
  }

  @Override
  public void setClip(int x, int y, int width, int height) {
    setClip(new Rectangle(x, y, width, height), false);
  }

  @Override
  public Shape getClip() {
    return getClipBounds();
  }

  @Override
  public void setClip(Shape clipShape) {
    if (clipShape instanceof Rectangle2D) {
      setClip((Rectangle2D) clipShape, false);
    } else if (clipShape == null) {
      setClip(null, false);
    } else {
      throw new IllegalArgumentException("Illegal shape for clip bounds, only java.awt.geom.Rectangle2D objects are supported");
    }
  }

  protected void setClip(Rectangle2D clipShape, boolean intersect) {
    if (clipShape == null) {
      clip = null;
      scissor(false);
    } else if (intersect && clip != null) {
      Rectangle rect = getTransform().createTransformedShape(clipShape).getBounds();
      clip = rect.intersection(clip);
      scissor(true);
    } else {
      clip = getTransform().createTransformedShape(clipShape).getBounds();
      scissor(true);
    }
  }

  protected void scissor(boolean enable) {
    if (enable) {
      gl.glScissor(clip.x, height - clip.y - clip.height, Math.max(clip.width, 0), Math.max(clip.height, 0));
      gl.glEnable(GL.GL_SCISSOR_TEST);
    } else {
      clip = null;
      gl.glDisable(GL.GL_SCISSOR_TEST);
    }
  }

  @Override
  public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    // glRasterPos* is transformed, but CopyPixels is not
    int x2 = x + dx;
    int y2 = y + dy + height;
    gl.glRasterPos2i(x2, y2);

    int x1 = x;
    int y1 = this.height - (y + height);
    gl.glCopyPixels(x1, y1, width, height, GL.GL_COLOR);
  }

  @Override
  public void drawLine(int x1, int y1, int x2, int y2) {
    shapeDrawer.drawLine(x1, y1, x2, y2);
  }

  @Override
  public void fillRect(int x, int y, int width, int height) {
    shapeDrawer.drawRect(x, y, width, height, true);
  }

  @Override
  public void clearRect(int x, int y, int width, int height) {
    setColor(gl, background, 1);
    fillRect(x, y, width, height);
    setColorRespectComposite(color);
  }

  @Override
  public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    shapeDrawer.drawRoundRect(x, y, width, height, arcWidth, arcHeight, false);
  }

  @Override
  public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    shapeDrawer.drawRoundRect(x, y, width, height, arcWidth, arcHeight, true);
  }

  @Override
  public void drawOval(int x, int y, int width, int height) {
    shapeDrawer.drawOval(x, y, width, height, false);
  }

  @Override
  public void fillOval(int x, int y, int width, int height) {
    shapeDrawer.drawOval(x, y, width, height, true);
  }

  @Override
  public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
    shapeDrawer.drawArc(x, y, width, height, startAngle, arcAngle, false);
  }

  @Override
  public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
    shapeDrawer.drawArc(x, y, width, height, startAngle, arcAngle, true);
  }

  @Override
  public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
    shapeDrawer.drawPolyline(xPoints, yPoints, nPoints);
  }

  @Override
  public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
    shapeDrawer.drawPolygon(xPoints, yPoints, nPoints, false);
  }

  @Override
  public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
    shapeDrawer.drawPolygon(xPoints, yPoints, nPoints, true);
  }

  @Override
  public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
    return imageDrawer.drawImage(img, xform, obs);
  }

  @Override
  public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
    // TODO Auto-generated method stub
  }

  @Override
  public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
    // TODO Auto-generated method stub
  }

  @Override
  public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
    // TODO Auto-generated method stub
  }

  @Override
  public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
    return imageDrawer.drawImage(img, x, y, null, observer);
  }

  @Override
  public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
    return imageDrawer.drawImage(img, x, y, bgcolor, observer);
  }

  @Override
  public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
    return imageDrawer.drawImage(img, x, y, width, height, null, observer);
  }

  @Override
  public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
    return imageDrawer.drawImage(img, x, y, width, height, bgcolor, observer);
  }

  @Override
  public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
    return imageDrawer.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null, observer);
  }

  @Override
  public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor,
      ImageObserver observer) {
    return imageDrawer.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
  }

  @Override
  public Graphics create() {
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glPushMatrix();
    gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
    gl.glPushClientAttrib((int) GL.GL_ALL_CLIENT_ATTRIB_BITS);

    GLGraphics2D newG2d = clone();

    for (G2DDrawingHelper helper : helpers) {
      helper.push(newG2d);
    }

    return newG2d;
  }

  @Override
  public void dispose() {
    /*
     * This is also called on the finalizer thread, which should not make OpenGL
     * calls. We also want to make sure that this only executes once.
     */
    if (!isDisposed && Threading.isOpenGLThread()) {
      isDisposed = true;

      if (parent != null) {
        for (G2DDrawingHelper helper : helpers) {
          helper.pop(parent);
        }
      }

      gl.glPopClientAttrib();
      gl.glPopAttrib();
      gl.glMatrixMode(GL.GL_MODELVIEW);
      gl.glPopMatrix();
    }
  }

  @Override
  protected GLGraphics2D clone() {
    try {
      GLGraphics2D clone = (GLGraphics2D) super.clone();
      clone.parent = this;
      clone.hints = (RenderingHints) hints.clone();
      return clone;
    } catch (CloneNotSupportedException exception) {
      throw new RuntimeException(exception);
    }
  }
}
