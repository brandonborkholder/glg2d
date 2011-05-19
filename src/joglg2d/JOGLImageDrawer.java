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
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;

/**
 * @author borkholder
 * @created Apr 27, 2010
 * 
 */
public class JOGLImageDrawer {
  private final GL gl;

  private TextureCache cache = new TextureCache();

  public JOGLImageDrawer(GL gl) {
    this.gl = gl;

    gl.glTexParameterf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_BLEND);
  }

  public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
    if (!isImageReady(img)) {
      return false;
    }

    return drawImage(img, AffineTransform.getTranslateInstance(x, y), bgcolor);
  }

  public boolean drawImage(Image img, AffineTransform xform, ImageObserver observer) {
    if (!isImageReady(img)) {
      return false;
    }

    return drawImage(img, xform, (Color) null);
  }

  public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
    if (!isImageReady(img)) {
      return false;
    }

    double imgHeight = img.getHeight(null);
    double imgWidth = img.getWidth(null);

    AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
    transform.scale(width / imgWidth, height / imgHeight);
    return drawImage(img, transform, bgcolor);
  }

  public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor,
      ImageObserver observer) {
    if (!isImageReady(img)) {
      return false;
    }

    Texture texture = getTexture(img);
    float height = texture.getHeight();
    float width = texture.getWidth();
    begin(texture, null, bgcolor);
    applyTexture(texture, dx1, dy1, dx2, dy2, sx1 / width, sy1 / height, sx2 / width, sy2 / height);
    end(texture);

    return true;
  }

  public void dispose() {
    for (Texture texture : cache.values()) {
      texture.dispose();
    }
  }

  protected boolean drawImage(Image img, AffineTransform xform, Color color) {
    Texture texture = getTexture(img);

    begin(texture, xform, color);
    applyTexture(texture);
    end(texture);

    return true;
  }

  protected void begin(Texture texture, AffineTransform xform, Color bgcolor) {
    gl.glEnable(GL.GL_TEXTURE_2D);
    texture.bind();

    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glPushMatrix();

    if (xform != null) {
      JOGLG2D.multMatrix(gl, xform);
    }

    if (bgcolor == null) {
      gl.glColor4f(1F, 1F, 1F, 1F);
    } else {
      JOGLG2D.setColor(gl, bgcolor);
    }
  }

  protected void end(Texture texture) {
    gl.glEnd();
    gl.glPopMatrix();
    gl.glDisable(GL.GL_TEXTURE_2D);
  }

  protected void applyTexture(Texture texture) {
    int width = texture.getWidth();
    int height = texture.getHeight();
    TextureCoords coords = texture.getImageTexCoords();

    applyTexture(texture, 0, 0, width, height, coords.left(), coords.top(), coords.right(), coords.bottom());
  }

  protected void applyTexture(Texture texture, int dx1, int dy1, int dx2, int dy2, float sx1, float sy1, float sx2, float sy2) {
    gl.glBegin(GL.GL_QUADS);

    // SW
    gl.glTexCoord2f(sx1, sy2);
    gl.glVertex2i(dx1, dy2);
    // SE
    gl.glTexCoord2f(sx2, sy2);
    gl.glVertex2i(dx2, dy2);
    // NE
    gl.glTexCoord2f(sx2, sy1);
    gl.glVertex2i(dx2, dy1);
    // NW
    gl.glTexCoord2f(sx1, sy1);
    gl.glVertex2i(dx1, dy1);

    gl.glEnd();
  }

  protected Texture getTexture(Image image) {
    Texture texture = cache.get(image);
    if (texture == null) {
      texture = createTexture(image);
      cache.put(image, texture);
    }

    return texture;
  }

  protected Texture createTexture(Image image) {
    BufferedImage bufferedImage;
    if (image instanceof BufferedImage) {
      bufferedImage = (BufferedImage) image;
    } else {
      bufferedImage = new BufferedImage(image.getHeight(null), image.getWidth(null), BufferedImage.TYPE_3BYTE_BGR);
      bufferedImage.createGraphics().drawImage(image, null, null);
    }

    Texture texture = TextureIO.newTexture(bufferedImage, false);
    return texture;
  }

  protected boolean isImageReady(Image img) {
    return img.getHeight(null) >= 0 && img.getWidth(null) >= 0;
  }

  @SuppressWarnings("serial")
  protected static class TextureCache extends HashMap<WeakKey<Image>, Texture> {
    private ReferenceQueue<Image> queue = new ReferenceQueue<Image>();

    public void expungeStaleEntries() {
      Reference<? extends Image> ref = queue.poll();
      while (ref != null) {
        remove(ref).dispose();
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
