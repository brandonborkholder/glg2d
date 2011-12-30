package glg2d.shaders;

import glg2d.G2DDrawingHelper;
import glg2d.GLGraphics2D;

public class ShaderRegistry implements G2DDrawingHelper {
  protected Shader fixedFunction;
  protected Shader texture;

  protected boolean needsRecompile;

  @Override
  public void setG2D(GLGraphics2D g2d) {
    if (fixedFunction == null) {
      fixedFunction = createFixedFunctionShader(g2d);
      needsRecompile = true;
    }

    if (texture == null) {
      texture = createTextureShader(g2d);
      needsRecompile = true;
    }

    if (needsRecompile) {
      fixedFunction.compileVertexShader();
      fixedFunction.compileFragmentShader();
      fixedFunction.createAndAttach();

      texture.compileVertexShader();
      texture.compileFragmentShader();
      texture.createAndAttach();
      
      needsRecompile = false;
    }
  }

  @Override
  public void dispose() {
    fixedFunction.delete();
    texture.delete();
    needsRecompile = true;
  }

  public Shader getFixedFunctionShader() {
    return fixedFunction;
  }

  public Shader getTextureShader() {
    return texture;
  }

  protected Shader createFixedFunctionShader(GLGraphics2D g2d) {
    BasicShader shader = new BasicShader(g2d.getGLContext().getGL());
    shader.readSources("FixedFuncShader");

    return shader;
  }

  protected Shader createTextureShader(GLGraphics2D g2d) {
    BasicShader shader = new BasicShader(g2d.getGLContext().getGL());
    shader.readSources("TextureShader");

    return shader;
  }

  @Override
  public void pop(GLGraphics2D parentG2d) {
    // nop
  }

  @Override
  public void push(GLGraphics2D newG2d) {
    // nop
  }
}
