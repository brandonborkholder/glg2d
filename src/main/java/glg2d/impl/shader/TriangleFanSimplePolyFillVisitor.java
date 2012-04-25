package glg2d.impl.shader;

import glg2d.SimplePathVisitor;
import glg2d.VertexBuffer;

import java.awt.BasicStroke;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

public class TriangleFanSimplePolyFillVisitor extends SimplePathVisitor implements ShaderPathVisitor {
  protected GL2ES2 gl;

  protected VertexBuffer vBuffer = VertexBuffer.getSharedBuffer();

  protected float[] color;
  protected FloatBuffer glMatrixData;

  protected ShapeShaderPipeline pipeline;

  public TriangleFanSimplePolyFillVisitor() {
    this(new TriangleFanPolyFillShader());
  }

  public TriangleFanSimplePolyFillVisitor(ShapeShaderPipeline pipeline) {
    this.pipeline = pipeline;
  }

  @Override
  public void setGLContext(GL context) {
    gl = context.getGL2ES2();

    if (!pipeline.isSetup()) {
      pipeline.setup(gl);
    }
  }

  @Override
  public void setColor(float[] rgba) {
    color = rgba;
  }

  @Override
  public void setTransform(FloatBuffer glMatrixBuffer) {
    glMatrixData = glMatrixBuffer;
  }

  @Override
  public void setStroke(BasicStroke stroke) {
    // nop
  }

  @Override
  public void beginPoly(int windingRule) {
    // do we need to care about winding rule?
    vBuffer.clear();
    pipeline.use(gl, true);

    pipeline.setColor(gl, color);
    pipeline.setGLTransform(gl, glMatrixData);
  }

  @Override
  public void moveTo(float[] vertex) {
    draw();

    vBuffer.addVertex(vertex, 0, 1);
  }

  @Override
  public void lineTo(float[] vertex) {
    vBuffer.addVertex(vertex, 0, 1);
  }

  @Override
  public void closeLine() {
    FloatBuffer buf = vBuffer.getBuffer();
    float x = buf.get(0);
    float y = buf.get(1);
    vBuffer.addVertex(x, y);
  }

  @Override
  public void endPoly() {
    draw();
    pipeline.use(gl, false);
  }

  protected void draw() {
    FloatBuffer buf = vBuffer.getBuffer();
    if (buf.position() == 0) {
      return;
    }

    buf.flip();

    pipeline.draw(gl, buf);
    vBuffer.clear();
  }
}
