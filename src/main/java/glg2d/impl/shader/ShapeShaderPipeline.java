package glg2d.impl.shader;

import java.awt.BasicStroke;
import java.nio.FloatBuffer;

import javax.media.opengl.GL2ES2;

public interface ShapeShaderPipeline extends ShaderPipeline {
  void setGLTransform(GL2ES2 gl, FloatBuffer glMatrixBuffer);

  void setColor(GL2ES2 gl, float[] rgba);

  void setStroke(GL2ES2 gl, BasicStroke stroke);

  void draw(GL2ES2 gl, FloatBuffer buffer);
}
