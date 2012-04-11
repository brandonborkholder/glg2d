/**************************************************************************
   Copyright 2012 Brandon Borkholder

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 ***************************************************************************/

package glg2d.impl.gl2;

import glg2d.GLGraphics2D;
import glg2d.impl.AbstractShapeHelper;

import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2GL3;

public class GL2ShapeDrawer extends AbstractShapeHelper {
  protected GL2 gl;

  protected FillSimpleConvexPolygonVisitor simpleFillVisitor;
  protected PolygonOrTesselatingVisitor complexFillVisitor;
  protected LineDrawingVisitor simpleStrokeVisitor;
  protected FastLineVisitor fastLineVisitor;

  public GL2ShapeDrawer() {
    simpleFillVisitor = new FillSimpleConvexPolygonVisitor();
    complexFillVisitor = new PolygonOrTesselatingVisitor();
    simpleStrokeVisitor = new LineDrawingVisitor();
    fastLineVisitor = new FastLineVisitor();
  }

  @Override
  public void setG2D(GLGraphics2D g2d) {
    super.setG2D(g2d);
    GL gl = g2d.getGLContext().getGL();
    simpleFillVisitor.setGLContext(gl);
    complexFillVisitor.setGLContext(gl);
    simpleStrokeVisitor.setGLContext(gl);
    fastLineVisitor.setGLContext(gl);
  }

  public void setAntiAlias(Object hintValue) {
    if (hintValue == RenderingHints.VALUE_ANTIALIAS_ON) {
      gl.glEnable(GL.GL_LINE_SMOOTH);
      gl.glEnable(GL2ES1.GL_POINT_SMOOTH);
      gl.glEnable(GL2GL3.GL_POLYGON_SMOOTH);
      gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
      gl.glHint(GL2ES1.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
      gl.glHint(GL2GL3.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
    } else {
      gl.glDisable(GL.GL_LINE_SMOOTH);
      gl.glDisable(GL2ES1.GL_POINT_SMOOTH);
      gl.glDisable(GL2GL3.GL_POLYGON_SMOOTH);
    }
  }

  @Override
  public void draw(Shape shape) {
    Stroke stroke = getStroke();
    if (stroke instanceof BasicStroke) {
      BasicStroke basicStroke = (BasicStroke) stroke;
      if (fastLineVisitor.isValid(basicStroke)) {
        fastLineVisitor.setStroke(basicStroke);
        traceShape(shape, fastLineVisitor);
        return;
      } else if (basicStroke.getDashArray() == null) {
        simpleStrokeVisitor.setStroke(basicStroke);
        traceShape(shape, simpleStrokeVisitor);
        return;
      }
    }

    // can fall through for various reasons
    fill(stroke.createStrokedShape(shape));
  }

  @Override
  protected void fill(Shape shape, boolean forceSimple) {
    if (forceSimple) {
      traceShape(shape, simpleFillVisitor);
    } else {
      traceShape(shape, complexFillVisitor);
    }
  }
}
