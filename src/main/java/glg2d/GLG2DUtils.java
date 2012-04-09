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

import java.awt.Color;
import java.awt.geom.AffineTransform;

import javax.media.opengl.GL2ES1;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

public class GLG2DUtils {
  public static void multMatrix(GLMatrixFunc gl, AffineTransform transform) {
    float[] matrix = new float[16];
    matrix[0] = (float) transform.getScaleX();
    matrix[1] = (float) transform.getShearY();
    matrix[4] = (float) transform.getShearX();
    matrix[5] = (float) transform.getScaleY();
    matrix[10] = 1;
    matrix[12] = (float) transform.getTranslateX();
    matrix[13] = (float) transform.getTranslateY();
    matrix[15] = 1;

    gl.glMultMatrixf(matrix, 0);
  }

  /**
   * Since the MODELVIEW matrix includes the transform from Java2D to OpenGL
   * coords, we remove that transform inline here.
   */
  public static AffineTransform getModelTransform(GL2ES1 gl, int canvasHeight) {
    float[] m = new float[16];
    gl.glGetFloatv(GLMatrixFunc.GL_MODELVIEW_MATRIX, m, 0);

    return new AffineTransform(m[0], -m[1], m[4], -m[5], m[12], canvasHeight - m[13]);
  }

  public static void setColor(GL2ES1 gl, Color c, float preMultiplyAlpha) {
    int rgb = c.getRGB();
    gl.glColor4ub((byte) (rgb >> 16 & 0xFF), (byte) (rgb >> 8 & 0xFF), (byte) (rgb & 0xFF), (byte) ((rgb >> 24 & 0xFF) * preMultiplyAlpha));
  }
}
