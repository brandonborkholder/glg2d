package glg2d.impl.shader.text;

import javax.media.opengl.GL2ES2;

import glg2d.impl.shader.AnyModePipeline;

public class TextPipeline extends AnyModePipeline {
  protected int xOffsetLocation = -1;
  protected int yOffsetLocation = -1;

  public TextPipeline() {
    this("TextShader.v", "FixedFuncShader.f");
  }

  public TextPipeline(String vertexShaderFilename, String fragmentShaderFilename) {
    super(vertexShaderFilename, fragmentShaderFilename);
  }

  public void setLocation(GL2ES2 gl, float x, float y) {
    if (xOffsetLocation >= 0) {
      gl.glUniform1f(xOffsetLocation, x);
    }

    if (yOffsetLocation >= 0) {
      gl.glUniform1f(yOffsetLocation, y);
    }
  }

  @Override
  protected void setupUniformsAndAttributes(GL2ES2 gl) {
    super.setupUniformsAndAttributes(gl);

    xOffsetLocation = gl.glGetUniformLocation(programId, "u_xoffset");
    yOffsetLocation = gl.glGetUniformLocation(programId, "u_yoffset");
  }
}
