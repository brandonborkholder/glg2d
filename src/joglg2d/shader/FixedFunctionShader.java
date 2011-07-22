package joglg2d.shader;

import javax.media.opengl.GL;

public class FixedFunctionShader extends AbstractShader {
  public FixedFunctionShader(GL gl, int shaderType) {
    super(getFixedFunctionShaderSource(shaderType), shaderType, gl);
  }

  public static String getFixedFunctionShaderSource(int type) {
    switch (type) {
    case GL.GL_VERTEX_SHADER:
      return "void main() { gl_Position = ftransform(); }";

    case GL.GL_FRAGMENT_SHADER:
      return "void main() { gl_FragColor = gl_Color; }";

    default:
      throw new IllegalArgumentException("Must be either GL_VERTEX_SHADER or GL_FRAGMENT_SHADER");
    }
  }
}
