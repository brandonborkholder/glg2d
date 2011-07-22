package joglg2d.shader;

import javax.media.opengl.GL;

public class AbstractShader {
  private int shaderId;

  protected String source;

  public AbstractShader(String source, int shaderType, GL gl) {
    this.source = source;

    shaderId = gl.glCreateShader(shaderType);
    gl.glShaderSource(shaderId, 1, new String[] { source }, null);
    gl.glCompileShader(shaderId);
    checkForError(gl, GL.GL_COMPILE_STATUS);
  }

  protected void checkForError(GL gl, int type) throws ShaderException {
    if (ShaderUtils.checkShaderError(gl, shaderId, type)) {
      throw new ShaderException(ShaderUtils.getShaderLog(gl, shaderId));
    }
  }

  public int getId() {
    return shaderId;
  }
}
