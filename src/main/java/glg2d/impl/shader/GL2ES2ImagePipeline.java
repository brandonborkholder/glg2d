package glg2d.impl.shader;

import static glg2d.GLG2DUtils.getGLColor;

import java.awt.Color;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import com.jogamp.opengl.util.GLArrayDataServer;
import com.jogamp.opengl.util.glsl.ShaderCode;

public class GL2ES2ImagePipeline extends AbstractShaderPipeline {
  protected GLArrayDataServer vertArrayData;
  protected GLArrayDataServer texArrayData;

  protected int transformLocation;
  protected int colorLocation;
  protected int textureLocation;

  public void setMatrix(GL2ES2 gl, FloatBuffer glMatrixData) {
    if (transformLocation >= 0) {
      gl.glUniformMatrix4fv(transformLocation, 1, false, glMatrixData);
    }
  }

  public void setColor(GL2ES2 gl, Color c) {
    float[] color = getGLColor(c);
    if (colorLocation >= 0) {
      gl.glUniform4fv(colorLocation, 1, color, 0);
    }
  }

  public void setTextureUnit(GL2ES2 gl, int unit) {
    if (textureLocation >= 0) {
      gl.glUniform1i(textureLocation, unit);
    }
  }

  public void bindVertCoords(GL2ES2 gl, FloatBuffer buffer) {
    vertArrayData = GLArrayDataServer.createGLSL("a_vertCoord", 2, GL.GL_FLOAT, false, buffer.limit() - buffer.position(),
        GL.GL_STATIC_DRAW);
    vertArrayData.put(buffer);
    vertArrayData.seal(gl, true);
  }

  public void bindTexCoords(GL2ES2 gl, FloatBuffer buffer) {
    texArrayData = GLArrayDataServer.createGLSL("a_texCoord", 2, GL.GL_FLOAT, false, buffer.limit() - buffer.position(), GL.GL_STATIC_DRAW);
    texArrayData.put(buffer);
    texArrayData.seal(gl, true);
  }

  @Override
  public void createProgramAndAttach(GL2ES2 gl) {
    super.createProgramAndAttach(gl);

    transformLocation = gl.glGetUniformLocation(program.id(), "u_transform");
    colorLocation = gl.glGetUniformLocation(program.id(), "u_color");
    textureLocation = gl.glGetUniformLocation(program.id(), "u_tex");
  }

  @Override
  protected void loadShaders(GL2ES2 gl) {
    vertexShader = ShaderCode.create(gl, GL2ES2.GL_VERTEX_SHADER, 1, getClass(), new String[] { "TextureShader.v" });
    fragmentShader = ShaderCode.create(gl, GL2ES2.GL_FRAGMENT_SHADER, 1, getClass(), new String[] { "TextureShader.f" });
  }

  public void draw(GL2ES2 gl) {
    gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4);
    vertArrayData.seal(gl, false);
    texArrayData.seal(gl, false);
  }
}
