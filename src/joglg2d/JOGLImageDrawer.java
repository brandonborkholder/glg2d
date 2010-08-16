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
  private static final AffineTransform IDENTITY = new AffineTransform();

  private final GL gl;

  public JOGLImageDrawer(GL gl) {
    this.gl = gl;
  }

  public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
    if (xform == null) {
      xform = IDENTITY;
    }

    BufferedImage bufferedImage = null;
    if (img instanceof BufferedImage) {
      bufferedImage = (BufferedImage) img;
    } else {
      bufferedImage = new BufferedImage(img.getHeight(obs), img.getWidth(obs), BufferedImage.TYPE_INT_RGB);
      bufferedImage.createGraphics().drawImage(img, xform, obs);
    }

    Texture texture = TextureIO.newTexture(bufferedImage, true);
    texture.enable();
    texture.bind();

    texture.setTexParameterf(GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
    gl.glColor3f(1, 1, 1);
//    gl.glEnable(GL.GL_BLEND);
    gl.glDisable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

    TextureCoords coords = texture.getImageTexCoords();

    gl.glBegin(GL.GL_QUADS);

    gl.glTexCoord2f(coords.left(), coords.bottom());
    gl.glVertex2d(0, bufferedImage.getHeight());
    gl.glTexCoord2f(coords.right(), coords.bottom());
    gl.glVertex2d(bufferedImage.getWidth(), bufferedImage.getHeight());
    gl.glTexCoord2f(coords.right(), coords.top());
    gl.glVertex2d(bufferedImage.getWidth(), 0);
    gl.glTexCoord2f(coords.left(), coords.top());
    gl.glVertex2d(0, 0);

    gl.glEnd();

    texture.disable();
    texture.dispose();

    return true;
  }
}
