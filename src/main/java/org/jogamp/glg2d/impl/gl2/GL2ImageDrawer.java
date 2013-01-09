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
package org.jogamp.glg2d.impl.gl2;


import java.awt.Color;
import java.awt.geom.AffineTransform;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.jogamp.glg2d.GLG2DUtils;
import org.jogamp.glg2d.impl.AbstractImageHelper;

import com.jogamp.opengl.util.texture.Texture;

public class GL2ImageDrawer extends AbstractImageHelper {
  @Override
  protected void begin(Texture texture, AffineTransform xform, Color bgcolor) {
    GL2 gl = g2d.getGLContext().getGL().getGL2();
    gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE);
    gl.glTexParameterf(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL.GL_BLEND);

    /*
     * FIXME This is unexpected since we never disable blending, but in some
     * cases it interacts poorly with multiple split panes, scroll panes and the
     * text renderer to disable blending.
     */
    g2d.setComposite(g2d.getComposite());

    texture.enable(gl);
    texture.bind(gl);

    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glPushMatrix();

    if (xform != null) {
      GLG2DUtils.multMatrix(gl, xform);
    }

    g2d.getColorHelper().setColorRespectComposite(bgcolor == null ? Color.white : bgcolor);
  }

  @Override
  protected void end(Texture texture) {
    GL2 gl = g2d.getGLContext().getGL().getGL2();
    gl.glEnd();
    gl.glPopMatrix();

    texture.disable(gl);
    g2d.getColorHelper().setColorRespectComposite(g2d.getColor());
  }

  @Override
  protected void applyTexture(Texture texture, int dx1, int dy1, int dx2, int dy2, float sx1, float sy1, float sx2, float sy2) {
    GL2 gl = g2d.getGLContext().getGL().getGL2();
    gl.glBegin(GL2.GL_QUADS);

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
}
