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

    gl.glEnable(GL.GL_TEXTURE_2D);
    gl.glTexParameterf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
  }

  public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
    int width = img.getWidth(null);
    int height = img.getHeight(null);

    Texture texture = getTexture(img);
    texture.bind();

    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glPushMatrix();

    JOGLG2D.multMatrix(gl, xform);

    gl.glColor3f(1, 1, 1);

    TextureCoords coords = texture.getImageTexCoords();

    gl.glBegin(GL.GL_QUADS);

    gl.glTexCoord2f(coords.left(), coords.bottom());
    gl.glVertex2d(0, height);
    gl.glTexCoord2f(coords.right(), coords.bottom());
    gl.glVertex2d(width, height);
    gl.glTexCoord2f(coords.right(), coords.top());
    gl.glVertex2d(width, 0);
    gl.glTexCoord2f(coords.left(), coords.top());
    gl.glVertex2d(0, 0);

    gl.glEnd();
    gl.glPopMatrix();

    return true;
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

  public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
    if (!isImageReady(img)) {
      return false;
    }

    double imgHeight = img.getHeight(null);
    double imgWidth = img.getWidth(null);

    AffineTransform transform = AffineTransform.getScaleInstance(width / imgWidth, height / imgHeight);
    transform.translate(x, y);
    return drawImage(img, transform, observer);
  }

  protected boolean isImageReady(Image img) {
    return img.getHeight(null) >= 0 && img.getWidth(null) >= 0;
  }
}
