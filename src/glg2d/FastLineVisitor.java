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

import java.awt.BasicStroke;

import javax.media.opengl.GL;

/**
 * Draws a line using the native GL implementation of a line. This is only
 * appropriate if the width of the line is less than a certain number of pixels
 * (not coordinate units) so that the user cannot see that the join and
 * endpoints are different. See {@link #isValid(BasicStroke)} for a set of
 * useful criteria.
 */
public class FastLineVisitor extends SimplePathVisitor {
  protected VertexBuffer buffer = VertexBuffer.getSharedBuffer();

  protected GL gl;

  @Override
  public void setGLContext(GL context) {
    gl = context;
  }

  @Override
  public void setStroke(BasicStroke stroke) {
    // already set the line width in isValid

    /*
     * Not perfect copy of the BasicStroke implementation, but it does get
     * decently close. The pattern is pretty much the same. I think it's pretty
     * much impossible to do with out a fragment shader and only the fixed
     * function pipeline.
     */
    float[] dash = stroke.getDashArray();
    if (dash != null) {
      float totalLength = 0;
      for (float f : dash) {
        totalLength += f;
      }

      float lengthSoFar = 0;
      int prevIndex = 0;
      int mask = 0;
      for (int i = 0; i < dash.length; i++) {
        lengthSoFar += dash[i];

        int nextIndex = (int) (lengthSoFar / totalLength * 16);
        for (int j = prevIndex; j < nextIndex; j++) {
          mask |= (~i & 1) << j;
        }

        prevIndex = nextIndex;
      }

      /*
       * XXX Should actually use the stroke phase, but not sure how yet.
       */

      gl.glEnable(GL.GL_LINE_STIPPLE);
      int factor = (int)totalLength;
      gl.glLineStipple(factor >> 4, (short) mask);
    }
  }

  /**
   * Returns {@code true} if this class can reasonably render the line. This
   * takes into account whether or not the transform will blow the line width
   * out of scale and it obvious that we aren't drawing correct corners and line
   * endings.
   *
   * <p>
   * Note: This must be called before {@link #setStroke(BasicStroke)}. If this
   * returns {@code false} then this renderer should not be used.
   * </p>
   */
  protected boolean isValid(BasicStroke stroke) {
    // if the dash length is odd, I don't know how to handle that yet
    float[] dash = stroke.getDashArray();
    if (dash != null && (dash.length & 1) == 1) {
      return false;
    }

    float[] matrix = new float[16];
    gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, matrix, 0);

    float scaleX = Math.abs(matrix[0]);
    float scaleY = Math.abs(matrix[5]);

    // scales are different, we can't get a good line width
    if (Math.abs(scaleX - scaleY) > 1e-6) {
      return false;
    }

    float strokeWidth = stroke.getLineWidth();

    // gl line width is in pixels, convert to pixel width
    float glLineWidth = strokeWidth * scaleX;

    // we'll only try if it's a thin line
    if (glLineWidth <= 3) {
      gl.glLineWidth(glLineWidth);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void moveTo(float[] vertex) {
    buffer.drawBuffer(gl, GL.GL_LINE_STRIP);
    buffer.addVertex(vertex, 0, 1);
  }

  @Override
  public void lineTo(float[] vertex) {
    buffer.addVertex(vertex, 0, 1);
  }

  @Override
  public void closeLine() {
    buffer.drawBuffer(gl, GL.GL_LINE_LOOP);
  }

  @Override
  public void beginPoly(int windingRule) {
    buffer.clear();
  }

  @Override
  public void endPoly() {
    buffer.drawBuffer(gl, GL.GL_LINE_STRIP);
    gl.glDisable(GL.GL_LINE_STIPPLE);
  }
}
