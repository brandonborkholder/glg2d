package glg2d.shaders;

import glg2d.G2DGLStringDrawer;
import glg2d.GLGraphics2D;

import java.awt.Color;

import com.sun.opengl.util.j2d.TextRenderer;

public class G2DShaderStringDrawer extends G2DGLStringDrawer {
  protected Shader shader;

  @Override
  public void setG2D(GLGraphics2D g2d) {
    super.setG2D(g2d);

    if (shader == null) {
      shader = ((GLShaderGraphics2D) g2d).getShaderRegistry().getTextureShader();
    }
  }

  @Override
  protected void begin(TextRenderer renderer, Color textColor) {
    super.begin(renderer, textColor);
    shader.use(true);
  }
  
  @Override
  protected void end(TextRenderer renderer) {
    shader.use(false);
    super.end(renderer);
  }
}
