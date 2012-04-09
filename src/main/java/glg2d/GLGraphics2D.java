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
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;
import javax.media.opengl.Threading;

/**
 * Implements the standard {@code Graphics2D} functionality, but instead draws
 * to an OpenGL canvas.
 */
public class GLGraphics2D extends Graphics2D implements Cloneable {
  protected GLGraphics2D parent;

  protected GLContext glContext;

  protected GL2 gl;

  protected int height;

  protected int width;

  protected boolean isDisposed;

  protected G2DGLShapeDrawer shapeDrawer;

  protected G2DGLImageDrawer imageDrawer;

  protected G2DGLStringDrawer stringDrawer;

  protected G2DGLTransformHelper matrixHelper;

  protected G2DGLColorHelper colorHelper;

  protected Rectangle clip;

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
    matrixHelper = new G2DGLTransformHelper();
    colorHelper = new G2DGLColorHelper();

    addG2DDrawingHelper(shapeDrawer);
    addG2DDrawingHelper(imageDrawer);
    addG2DDrawingHelper(stringDrawer);
    addG2DDrawingHelper(matrixHelper);
    addG2DDrawingHelper(colorHelper);
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
    gl = glContext.getGL().getGL2();

    for (G2DDrawingHelper helper : helpers) {
      helper.setG2D(this);
    }
  }

  protected void prePaint(GLAutoDrawable drawable, Component component) {
    setCanvas(drawable);
    setupState(component);
  }

  protected void setupState(Component component) {
    // push all GL states
    gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
    gl.glPushClientAttrib((int) GL2.GL_ALL_CLIENT_ATTRIB_BITS);

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
    gl.glDisable(GL2ES1.GL_ALPHA_TEST);
    gl.glDisable(GL.GL_DEPTH_TEST);
    gl.glDisable(GL.GL_CULL_FACE);
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

  public void glDispose() {
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
    stringDrawer.drawString(str, getColor(), x, y);
  }

  @Override
  public void drawString(String str, float x, float y) {
    stringDrawer.drawString(str, getColor(), x, y);
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
    return colorHelper.getComposite();
  }

  @Override
  public void setComposite(Composite comp) {
    colorHelper.setComposite(comp);
  }

  @Override
  public void setPaint(Paint paint) {
    colorHelper.setPaint(paint);
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
    matrixHelper.translate(x, y);
  }

  @Override
  public void translate(double x, double y) {
    matrixHelper.translate(x, y);
  }

  @Override
  public void rotate(double theta) {
    matrixHelper.rotate(theta);
  }

  @Override
  public void rotate(double theta, double x, double y) {
    matrixHelper.rotate(theta, x, y);
  }

  @Override
  public void scale(double sx, double sy) {
    matrixHelper.scale(sx, sy);
  }

  @Override
  public void shear(double shx, double shy) {
    matrixHelper.shear(shx, shy);
  }

  @Override
  public void transform(AffineTransform Tx) {
    matrixHelper.transform(Tx);
  }

  @Override
  public void setTransform(AffineTransform transform) {
    matrixHelper.setTransform(transform);
  }

  @Override
  public AffineTransform getTransform() {
    return matrixHelper.getTransform();
  }

  @Override
  public Paint getPaint() {
    return colorHelper.getPaint();
  }

  @Override
  public Color getColor() {
    return colorHelper.getColor();
  }

  @Override
  public void setColor(Color c) {
    colorHelper.setColor(c);
  }

  @Override
  public void setBackground(Color color) {
    colorHelper.setBackground(color);
  }

  @Override
  public Color getBackground() {
    return colorHelper.getBackground();
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
    colorHelper.setPaintMode();
  }

  @Override
  public void setXORMode(Color c) {
    colorHelper.setXORMode(c);
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
        double[] pts = new double[8];
        pts[0] = clip.getMinX();
        pts[1] = clip.getMinY();
        pts[2] = clip.getMaxX();
        pts[3] = clip.getMinY();
        pts[4] = clip.getMaxX();
        pts[5] = clip.getMaxY();
        pts[6] = clip.getMinX();
        pts[7] = clip.getMaxY();
        getTransform().inverseTransform(pts, 0, pts, 0, 4);
        int minX = (int) Math.min(pts[0], Math.min(pts[2], Math.min(pts[4], pts[6])));
        int maxX = (int) Math.max(pts[0], Math.max(pts[2], Math.max(pts[4], pts[6])));
        int minY = (int) Math.min(pts[1], Math.min(pts[3], Math.min(pts[5], pts[7])));
        int maxY = (int) Math.max(pts[1], Math.max(pts[3], Math.max(pts[5], pts[7])));
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
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
    colorHelper.copyArea(x, y, width, height, dx, dy);
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
    Color c = getColor();
    colorHelper.setColorNoRespectComposite(getBackground());
    fillRect(x, y, width, height);
    colorHelper.setColorRespectComposite(c);
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
    gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
    gl.glPushClientAttrib((int) GL2.GL_ALL_CLIENT_ATTRIB_BITS);

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
