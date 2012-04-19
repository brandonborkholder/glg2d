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

package glg2d.impl.shader;

import glg2d.GLGraphics2D;
import glg2d.impl.AbstractImageHelper;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.texture.Texture;

public class GL2ES2ImageDrawer extends AbstractImageHelper {
  protected FloatBuffer buffer = Buffers.newDirectFloatBuffer(300);
  protected GL2ES2ImagePipeline shader;

  private float[] white = new float[] { 1, 1, 1, 1 };

  public GL2ES2ImageDrawer(GL2ES2ImagePipeline shader) {
    this.shader = shader;
  }

  @Override
  public void setG2D(GLGraphics2D g2d) {
    super.setG2D(g2d);

    GL2ES2 gl = g2d.getGLContext().getGL().getGL2ES2();
    if (!shader.isSetup()) {
      shader.setup(gl);
    }
  }

  @Override
  protected void begin(Texture texture, AffineTransform xform, Color bgcolor) {
    /*
     * FIXME This is unexpected since we never disable blending, but in some
     * cases it interacts poorly with multiple split panes, scroll panes and the
     * text renderer to disable blending.
     */
    g2d.setComposite(g2d.getComposite());

    GL2ES2 gl = g2d.getGLContext().getGL().getGL2ES2();
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);

    gl.glActiveTexture(GL.GL_TEXTURE0);
    texture.enable(gl);
    texture.bind(gl);

    shader.use(gl, true);

    float[] rgba;
    if (bgcolor == null) {
      rgba = white;
      rgba[3] = ((GL2ES2ColorHelper) g2d.getColorHelper()).getCompositeAlpha();
    } else {
      rgba = ((GL2ES2ColorHelper) g2d.getColorHelper()).getForegroundRGBA();
    }

    FloatBuffer matrixBuf = ((GL2ES2TransformHelper) g2d.getMatrixHelper()).getGLMatrixData(xform);

    shader.setColor(gl, rgba);
    shader.setMatrix(gl, matrixBuf);
    shader.setTextureUnit(gl, 0);
  }

  @Override
  protected void applyTexture(Texture texture, int dx1, int dy1, int dx2, int dy2, float sx1, float sy1, float sx2, float sy2) {
    GL2ES2 gl = g2d.getGLContext().getGL().getGL2ES2();

    buffer.rewind();

    // vertex values
    buffer.put(dx1);
    buffer.put(dy1);
    buffer.put(dx1);
    buffer.put(dy2);
    buffer.put(dx2);
    buffer.put(dy1);
    buffer.put(dx2);
    buffer.put(dy2);

    // texture values
    buffer.put(sx1);
    buffer.put(sy1);
    buffer.put(sx1);
    buffer.put(sy2);
    buffer.put(sx2);
    buffer.put(sy1);
    buffer.put(sx2);
    buffer.put(sy2);

    buffer.limit(8);
    buffer.position(0);
    shader.bindVertCoords(gl, buffer.slice());

    buffer.limit(16);
    buffer.position(8);
    shader.bindTexCoords(gl, buffer.slice());

    buffer.limit(buffer.capacity());

    shader.draw(gl);
  }

  @Override
  protected void end(Texture texture) {
    GL2ES2 gl = g2d.getGLContext().getGL().getGL2ES2();
    shader.use(gl, false);
    texture.disable(gl);
  }
}
