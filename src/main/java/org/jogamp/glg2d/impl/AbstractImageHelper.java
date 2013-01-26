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
package org.jogamp.glg2d.impl;

import static org.jogamp.glg2d.impl.GLG2DNotImplemented.notImplemented;

import java.awt.Color;
import java.awt.Image;
import java.awt.RenderingHints.Key;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import org.jogamp.glg2d.GLG2DImageHelper;
import org.jogamp.glg2d.GLGraphics2D;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public abstract class AbstractImageHelper implements GLG2DImageHelper {
  /**
   * This cache is kept for each paint operation. We don't keep track of images
   * being changed across different painting calls. The first time we see an
   * image, we cache the texture. Then we clear the cache for the next call to
   * {@code display()}.
   */
  protected TextureCache cache = new TextureCache();

  protected GLGraphics2D g2d;

  protected abstract void begin(Texture texture, AffineTransform xform, Color bgcolor);

  protected abstract void applyTexture(Texture texture, int dx1, int dy1, int dx2, int dy2, float sx1, float sy1,
      float sx2, float sy2);

  protected abstract void end(Texture texture);

  @Override
  public void setG2D(GLGraphics2D g2d) {
    this.g2d = g2d;
    cache.clear();
  }

  @Override
  public void push(GLGraphics2D newG2d) {
    // nop
  }

  @Override
  public void pop(GLGraphics2D parentG2d) {
    // nop
  }

  @Override
  public void setHint(Key key, Object value) {
    // nop
  }

  @Override
  public void resetHints() {
    // nop
  }

  @Override
  public void dispose() {
    cache.clear();
  }

  @Override
  public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
    return drawImage(img, AffineTransform.getTranslateInstance(x, y), bgcolor, observer);
  }

  @Override
  public boolean drawImage(Image img, AffineTransform xform, ImageObserver observer) {
    return drawImage(img, xform, (Color) null, observer);
  }

  @Override
  public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
    double imgHeight = img.getHeight(null);
    double imgWidth = img.getWidth(null);

    if (imgHeight < 0 || imgWidth < 0) {
      return false;
    }

    AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
    transform.scale(width / imgWidth, height / imgHeight);
    return drawImage(img, transform, bgcolor, observer);
  }

  @Override
  public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2,
      int sy2, Color bgcolor, ImageObserver observer) {
    Texture texture = getTexture(img, observer);
    if (texture == null) {
      return false;
    }

    float height = texture.getHeight();
    float width = texture.getWidth();
    begin(texture, null, bgcolor);
    applyTexture(texture, dx1, dy1, dx2, dy2, sx1 / width, sy1 / height, sx2 / width, sy2 / height);
    end(texture);

    return true;
  }

  protected boolean drawImage(Image img, AffineTransform xform, Color color, ImageObserver observer) {
    Texture texture = getTexture(img, observer);

    begin(texture, xform, color);
    applyTexture(texture);
    end(texture);

    return true;
  }

  protected void applyTexture(Texture texture) {
    int width = texture.getWidth();
    int height = texture.getHeight();
    TextureCoords coords = texture.getImageTexCoords();

    applyTexture(texture, 0, 0, width, height, coords.left(), coords.top(), coords.right(), coords.bottom());
  }

  /**
   * Cache the texture if possible. I have a feeling this will run into issues
   * later as images change. Just not sure how to handle it if they do. I
   * suspect I should be using the ImageConsumer class and dumping pixels to the
   * screen as I receive them.
   * 
   * <p>
   * If an image is a BufferedImage, turn it into a texture and cache it. If
   * it's not, draw it to a BufferedImage and see if all the image data is
   * available. If it is, cache it. If it's not, don't cache it. But if not all
   * the image data is available, we will draw it what we have, since we draw
   * anything in the image to a BufferedImage.
   * </p>
   */
  protected Texture getTexture(Image image, ImageObserver observer) {
    Texture texture = cache.get(image);
    if (texture == null) {
      BufferedImage bufferedImage;
      if (image instanceof BufferedImage && ((BufferedImage) image).getType() != BufferedImage.TYPE_CUSTOM) {
        bufferedImage = (BufferedImage) image;
      } else {
        bufferedImage = toBufferedImage(image);
      }

      texture = AWTTextureIO.newTexture(g2d.getGLContext().getGL().getGLProfile(), bufferedImage, false);
      cache.put(image, texture);
    }
    

    return texture;
  }

  protected BufferedImage toBufferedImage(Image image) {
    int width = image.getWidth(null);
    int height = image.getHeight(null);
    if (width < 0 || height < 0) {
      return null;
    }

    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    bufferedImage.createGraphics().drawImage(image, null, null);
    return bufferedImage;
  }

  @Override
  public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
    // TODO
    notImplemented("drawImage(BufferedImage, BufferedImageOp, int, int)");
  }

  @Override
  public void drawImage(RenderedImage img, AffineTransform xform) {
    // TODO
    notImplemented("drawImage(RenderedImage, AffineTransform)");
  }

  @Override
  public void drawImage(RenderableImage img, AffineTransform xform) {
    // TODO
    notImplemented("drawImage(RenderableImage, AffineTransform)");
  }

  @SuppressWarnings("serial")
  protected static class TextureCache extends HashMap<WeakKey<Image>, Texture> {
    private ReferenceQueue<Image> queue = new ReferenceQueue<Image>();

    public void expungeStaleEntries() {
      Reference<? extends Image> ref = queue.poll();
      while (ref != null) {
        remove(ref);
        ref = queue.poll();
      }
    }

    public Texture get(Image image) {
      expungeStaleEntries();
      WeakKey<Image> key = new WeakKey<Image>(image, null);
      return get(key);
    }

    public Texture put(Image image, Texture texture) {
      expungeStaleEntries();
      WeakKey<Image> key = new WeakKey<Image>(image, queue);
      return put(key, texture);
    }
  }

  protected static class WeakKey<T> extends WeakReference<T> {
    private final int hash;

    public WeakKey(T value, ReferenceQueue<T> queue) {
      super(value, queue);
      hash = value.hashCode();
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if (obj instanceof WeakKey) {
        WeakKey<?> other = (WeakKey<?>) obj;
        return other.hash == hash && get() == other.get();
      } else {
        return false;
      }
    }
  }

}