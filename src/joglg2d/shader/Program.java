package joglg2d.shader;

import javax.media.opengl.GL;

public class Program {
  private int programId;

  public void attach(GL gl, AbstractShader shader) {
    programId = gl.glCreateProgram();
    gl.glAttachShader(programId, shader.getId());
  }

  public void link(GL gl) {
    gl.glLinkProgram(programId);
    checkForError(gl, GL.GL_LINK_STATUS);
    gl.glValidateProgram(programId);
    checkForError(gl, GL.GL_VALIDATE_STATUS);
  }

  public void use(GL gl) {
    gl.glUseProgram(programId);
  }

  public void stopUsing(GL gl) {
    stopUsingShaders(gl);
  }

  protected void checkForError(GL gl, int type) throws ShaderException {
    if (ShaderUtils.checkProgramError(gl, programId, type)) {
      throw new ShaderException(ShaderUtils.getProgramLog(gl, programId));
    }
  }

  public static void stopUsingShaders(GL gl) {
    gl.glUseProgram(0);
  }
}
