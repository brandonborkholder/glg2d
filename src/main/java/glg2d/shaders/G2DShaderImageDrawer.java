package glg2d.shaders;

import glg2d.G2DGLImageDrawer;
import glg2d.GLGraphics2D;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import com.sun.opengl.util.texture.Texture;

public class G2DShaderImageDrawer extends G2DGLImageDrawer {
  protected Shader shader;
  
  @Override
  public void setG2D(GLGraphics2D g2d) {
    super.setG2D(g2d);

    if (shader == null) {
      shader = ((GLShaderGraphics2D) g2d).getShaderRegistry().getTextureShader();
    }
  }

  @Override
  protected void begin(Texture texture, AffineTransform xform, Color bgcolor) {
    super.begin(texture, xform, bgcolor);
    shader.use(true);
  }
  
  @Override
  protected void end(Texture texture) {
    shader.use(false);
    super.end(texture);
  }
}
