/*
 * Copyright 2012 Brandon Borkholder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package glg2d.impl.shader;

import glg2d.GLGraphics2D;
import glg2d.impl.AbstractShapeHelper;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;

public class GL2ES2ShapeDrawer extends AbstractShapeHelper {
  protected ShaderPathVisitor lineVisitor;
  protected ShaderPathVisitor simpleFillVisitor;

  protected GLGraphics2D g2d;

  public GL2ES2ShapeDrawer() {
    lineVisitor = new ShaderLineVisitor();
    simpleFillVisitor = new TriangleFanSimplePolyFillVisitor();
  }

  @Override
  public void setG2D(GLGraphics2D g2d) {
    this.g2d = g2d;
    super.setG2D(g2d);
    lineVisitor.setGLContext(g2d.getGLContext().getGL());
    simpleFillVisitor.setGLContext(g2d.getGLContext().getGL());
  }

  public void draw(Shape shape) {
    Stroke stroke = getStroke();
    if (stroke instanceof BasicStroke) {
      lineVisitor.setStroke((BasicStroke) stroke);
      lineVisitor.setColor(((GL2ES2ColorHelper) g2d.getColorHelper()).getForegroundRGBA());
      lineVisitor.setTransform(((GL2ES2TransformHelper) g2d.getMatrixHelper()).getGLMatrixData());
      traceShape(shape, lineVisitor);
    } else {
      fill(stroke.createStrokedShape(shape), false);
    }
  }

  @Override
  protected void fill(Shape shape, boolean isDefinitelySimpleConvex) {
    if (isDefinitelySimpleConvex) {
      simpleFillVisitor.setColor(((GL2ES2ColorHelper) g2d.getColorHelper()).getForegroundRGBA());
      simpleFillVisitor.setTransform(((GL2ES2TransformHelper) g2d.getMatrixHelper()).getGLMatrixData());
      traceShape(shape, simpleFillVisitor);
    }
  }
}
