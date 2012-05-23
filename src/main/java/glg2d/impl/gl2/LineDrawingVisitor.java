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

package glg2d.impl.gl2;

import glg2d.impl.BasicStrokeLineVisitor;

import java.awt.BasicStroke;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import com.jogamp.common.nio.Buffers;

/**
 * Draws a line, as outlined by a {@link BasicStroke}. The current
 * implementation supports everything except dashes. This class draws a series
 * of quads for each line segment, joins corners and endpoints as appropriate.
 */
public class LineDrawingVisitor extends BasicStrokeLineVisitor {
  protected GL2 gl;

  protected int deviceBufferId;

  @Override
  public void setGLContext(GL context) {
    gl = context.getGL2();

    if (!gl.glIsBuffer(deviceBufferId)) {
      int[] ids = new int[1];
      gl.glGenBuffers(1, ids, 0);
      deviceBufferId = ids[0];
    }
  }

  @Override
  public void beginPoly(int windingRule) {
    /*
     * pen hangs down and to the right. See java.awt.Graphics
     */
    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glPushMatrix();
    gl.glTranslatef(0.5f, 0.5f, 0);

    super.beginPoly(windingRule);
  }

  @Override
  public void endPoly() {
    super.endPoly();

    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glPopMatrix();
  }

  @Override
  protected void drawBuffer() {
    FloatBuffer buf = vBuffer.getBuffer();
    int count = buf.limit() - buf.position();

    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, deviceBufferId);

    gl.glBufferData(GL.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT * count, buf, GL2ES2.GL_STREAM_DRAW);
    gl.glVertexPointer(2, GL.GL_FLOAT, 0, 0);
    gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, count / 2);

    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
  }
}
