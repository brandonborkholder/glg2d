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

import java.awt.BasicStroke;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;

import com.jogamp.opengl.util.GLArrayDataServer;

public class GL2ES2StrokeLinePipeline extends AbstractShaderPipeline {
  protected GLArrayDataServer vertArrayData;

  protected int maxVerticesOut = 1024;

  protected int vertCoordLocation;
  protected int vertCoordBuffer;

  protected int transformLocation;
  protected int colorLocation;
  protected int lineWidthLocation;
  protected int miterLimitLocation;
  protected int joinTypeLocation;

  public GL2ES2StrokeLinePipeline() {
    this("StrokeShader.v", "StrokeShader.g", "StrokeShader.f");
  }

  public GL2ES2StrokeLinePipeline(String vertexShaderFileName, String geometryShaderFileName, String fragmentShaderFileName) {
    super(vertexShaderFileName, geometryShaderFileName, fragmentShaderFileName);
  }

  public void setTransform(GL2ES2 gl, FloatBuffer glMatrixData) {
    if (transformLocation >= 0) {
      gl.glUniformMatrix4fv(transformLocation, 1, false, glMatrixData);
    }
  }

  public void setColor(GL2ES2 gl, float[] rgba) {
    if (colorLocation >= 0) {
      gl.glUniform4fv(colorLocation, 1, rgba, 0);
    }
  }

  public void setStroke(GL2ES2 gl, BasicStroke stroke) {
    if (lineWidthLocation >= 0) {
      gl.glUniform1f(lineWidthLocation, stroke.getLineWidth());
    }

    if (miterLimitLocation >= 0) {
      gl.glUniform1f(miterLimitLocation, stroke.getMiterLimit());
    }

    if (joinTypeLocation >= 0) {
      gl.glUniform1i(joinTypeLocation, stroke.getLineJoin());
    }
  }

  protected void bindBuffer(GL2ES2 gl, FloatBuffer vertexBuffer) {
    gl.glEnableVertexAttribArray(vertCoordLocation);
    gl.glVertexAttribPointer(vertCoordLocation, 2, GL.GL_FLOAT, false, 0, vertexBuffer);
  }

  public void draw(GL2ES2 gl, FloatBuffer vertexBuffer) {
    int numPts = (vertexBuffer.limit() - vertexBuffer.position()) / 2;
    bindBuffer(gl, vertexBuffer);

    // offset and draw each segment with adjacent points
    gl.glDrawArrays(GL.GL_TRIANGLES, 0, numPts);
    gl.glDrawArrays(GL.GL_TRIANGLES, 1, numPts - 1);
    gl.glDrawArrays(GL.GL_TRIANGLES, 2, numPts - 2);

    gl.glDisableVertexAttribArray(vertCoordLocation);
  }

  @Override
  protected void setupUniformsAndAttributes(GL2ES2 gl) {
    super.setupUniformsAndAttributes(gl);

    transformLocation = gl.glGetUniformLocation(programId, "u_transform");
    colorLocation = gl.glGetUniformLocation(programId, "u_color");
    lineWidthLocation = gl.glGetUniformLocation(programId, "u_lineWidth");
    miterLimitLocation = gl.glGetUniformLocation(programId, "u_miterLimit");
    joinTypeLocation = gl.glGetUniformLocation(programId, "u_joinType");

    vertCoordLocation = gl.glGetAttribLocation(programId, "a_vertCoord");
  }

  @Override
  protected void attachShaders(GL2ES2 gl) {
    super.attachShaders(gl);

    GL2GL3 gl3 = gl.getGL2GL3();
    gl3.glProgramParameteri(programId, GL2GL3.GL_GEOMETRY_INPUT_TYPE_ARB, GL.GL_TRIANGLES);
    gl3.glProgramParameteri(programId, GL2GL3.GL_GEOMETRY_OUTPUT_TYPE_ARB, GL.GL_TRIANGLE_STRIP);
    gl3.glProgramParameteri(programId, GL2GL3.GL_GEOMETRY_VERTICES_OUT_ARB, maxVerticesOut);
  }

  @Override
  public void delete(GL2ES2 gl) {
    super.delete(gl);

    if (gl.glIsBuffer(vertCoordBuffer)) {
      gl.glDeleteBuffers(1, new int[] { vertCoordBuffer }, 0);
    }
  }
}
