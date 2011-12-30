package glg2d.shaders;

import glg2d.GLGraphics2D;

import javax.media.opengl.GLAutoDrawable;

public class GLShaderGraphics2D extends GLGraphics2D {
  protected ShaderRegistry shaders;

  public GLShaderGraphics2D(int width, int height) {
    super(width, height);

    shaders = new ShaderRegistry();
    
    imageDrawer = new G2DShaderImageDrawer();
    stringDrawer = new G2DShaderStringDrawer();
  }

  @Override
  protected void setCanvas(GLAutoDrawable drawable) {
    glContext = drawable.getContext();
    gl = glContext.getGL();

    shaders.setG2D(this);
    super.setCanvas(drawable);
  }

  public ShaderRegistry getShaderRegistry() {
    return shaders;
  }
}
