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
package glg2d.impl.gl2;

import glg2d.impl.BasicStrokeLineVisitor;

import java.awt.BasicStroke;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

/**
 * Draws a line, as outlined by a {@link BasicStroke}. The current
 * implementation supports everything except dashes. This class draws a series
 * of quads for each line segment, joins corners and endpoints as appropriate.
 */
public class LineDrawingVisitor extends BasicStrokeLineVisitor {
  protected GL2 gl;

  @Override
  public void setGLContext(GL context) {
    gl = context.getGL2();
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
    vBuffer.drawBuffer(gl, GL.GL_TRIANGLE_STRIP);
  }
}
