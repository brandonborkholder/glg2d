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
package glg2d.impl.shader;

import java.awt.BasicStroke;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.GLArrayDataServer;

public class GL2ES2StrokeLinePipeline extends AbstractShaderPipeline {
  private static final int DRAW_BOTH = 0;
  private static final int DRAW_FIRST = -1;
  private static final int DRAW_LAST = 1;

  protected FloatBuffer vBuffer = Buffers.newDirectFloatBuffer(500);

  protected GLArrayDataServer vertArrayData;

  protected String lineCapShaderFileName;
  protected int lineCapShaderId;

  protected int maxVerticesOut = 32;

  protected int vertCoordLocation;
  protected int vertBeforeLocation;
  protected int vertAfterLocation;
  protected int vertCoordBuffer;

  protected int transformLocation;
  protected int colorLocation;
  protected int lineWidthLocation;
  protected int miterLimitLocation;
  protected int joinTypeLocation;
  protected int drawEndLocation;

  public GL2ES2StrokeLinePipeline() {
    this("StrokeShader.v", "StrokeShader.g", "CapShader.g", "StrokeShader.f");
  }

  public GL2ES2StrokeLinePipeline(String vertexShaderFileName, String lineJoinShaderFileName, String lineCapShaderFileName,
      String fragmentShaderFileName) {
    super(vertexShaderFileName, lineJoinShaderFileName, fragmentShaderFileName);
    this.lineCapShaderFileName = lineCapShaderFileName;
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

  protected void setDrawEnd(GL2ES2 gl, int drawType) {
    if (drawEndLocation >= 0) {
      gl.glUniform1i(drawEndLocation, drawType);
    }
  }

  protected void bindBuffer(GL2ES2 gl, FloatBuffer vertexBuffer) {
    gl.glEnableVertexAttribArray(vertCoordLocation);
    gl.glEnableVertexAttribArray(vertBeforeLocation);
    gl.glEnableVertexAttribArray(vertAfterLocation);

    if (!gl.glIsBuffer(vertCoordBuffer)) {
      int[] ids = new int[1];
      gl.glGenBuffers(1, ids, 0);
      vertCoordBuffer = ids[0];
    }

    int count = vertexBuffer.limit() - vertexBuffer.position();
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertCoordBuffer);
    gl.glBufferData(GL.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT * count, vertexBuffer, GL2ES2.GL_STREAM_DRAW);

    gl.glVertexAttribPointer(vertCoordLocation, 2, GL.GL_FLOAT, false, 0, 2 * Buffers.SIZEOF_FLOAT);
    gl.glVertexAttribPointer(vertBeforeLocation, 2, GL.GL_FLOAT, false, 0, 0);
    gl.glVertexAttribPointer(vertAfterLocation, 2, GL.GL_FLOAT, false, 0, 4 * Buffers.SIZEOF_FLOAT);
  }

  public void draw(GL2ES2 gl, FloatBuffer vertexBuffer, boolean close) {
    int pos = vertexBuffer.position();
    int lim = vertexBuffer.limit();
    int numPts = (lim - pos) / 2;

    if (numPts * 2 + 4 > vBuffer.capacity()) {
      vBuffer = Buffers.newDirectFloatBuffer(numPts * 2 + 4);
    }

    vBuffer.clear();

    vBuffer.put(vertexBuffer.get(lim - 2));
    vBuffer.put(vertexBuffer.get(lim - 1));
    vBuffer.put(vertexBuffer);
    vBuffer.put(vertexBuffer.get(pos));
    vBuffer.put(vertexBuffer.get(pos + 1));

    vBuffer.flip();

    bindBuffer(gl, vBuffer);

    if (close) {
      setDrawEnd(gl, DRAW_BOTH);
//      if ((numPts & 1) == 0) {
        gl.glDrawArrays(GL.GL_LINES, 1, numPts);
        gl.glDrawArrays(GL.GL_LINES, 2, numPts - 2);
//      } else {
//        gl.glDrawArrays(GL.GL_LINES, 1, numPts - 1);
//        gl.glDrawArrays(GL.GL_LINES, 2, numPts - 1);
//      }
    } else {
      setDrawEnd(gl, DRAW_BOTH);
      gl.glDrawArrays(GL.GL_LINES, 2, numPts - 1);
      gl.glDrawArrays(GL.GL_LINES, 3, numPts - 2);

      setDrawEnd(gl, DRAW_LAST);
      gl.glDrawArrays(GL.GL_LINES, 1, 1);

      setDrawEnd(gl, DRAW_FIRST);
      gl.glDrawArrays(GL.GL_LINES, numPts, 1);
    }

    gl.glDisableVertexAttribArray(vertCoordLocation);
    gl.glDisableVertexAttribArray(vertBeforeLocation);
    gl.glDisableVertexAttribArray(vertAfterLocation);
    
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
  }

  @Override
  protected void setupUniformsAndAttributes(GL2ES2 gl) {
    super.setupUniformsAndAttributes(gl);

    transformLocation = gl.glGetUniformLocation(programId, "u_transform");
    colorLocation = gl.glGetUniformLocation(programId, "u_color");
    lineWidthLocation = gl.glGetUniformLocation(programId, "u_lineWidth");
    miterLimitLocation = gl.glGetUniformLocation(programId, "u_miterLimit");
    joinTypeLocation = gl.glGetUniformLocation(programId, "u_joinType");
    drawEndLocation = gl.glGetUniformLocation(programId, "u_drawEnd");

    vertCoordLocation = gl.glGetAttribLocation(programId, "a_vertCoord");
    vertBeforeLocation = gl.glGetAttribLocation(programId, "a_vertBefore");
    vertAfterLocation = gl.glGetAttribLocation(programId, "a_vertAfter");
  }

  @Override
  protected void attachShaders(GL2ES2 gl) {
    super.attachShaders(gl);

    if (lineCapShaderFileName != null) {
      lineCapShaderId = compileShader(gl, GL2GL3.GL_GEOMETRY_SHADER_ARB, getClass(), lineCapShaderFileName);
    }

    GL2GL3 gl3 = gl.getGL2GL3();
    gl3.glProgramParameteri(programId, GL2GL3.GL_GEOMETRY_INPUT_TYPE_ARB, GL.GL_LINES);
    gl3.glProgramParameteri(programId, GL2GL3.GL_GEOMETRY_OUTPUT_TYPE_ARB, GL.GL_TRIANGLE_STRIP);
    gl3.glProgramParameteri(programId, GL2GL3.GL_GEOMETRY_VERTICES_OUT_ARB, maxVerticesOut);
  }

  @Override
  public void delete(GL2ES2 gl) {
    super.delete(gl);

    gl.glDeleteBuffers(1, new int[] { vertCoordBuffer }, 0);
  }
}
