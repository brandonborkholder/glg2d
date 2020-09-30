/*
 * Copyright 2015 Brandon Borkholder
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
package net.opengrabeso.glg2d.impl.gl2;


import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;

import com.github.opengrabeso.jaagl.GL;
import com.github.opengrabeso.jaagl.GL2;

import net.opengrabeso.glg2d.impl.AbstractShapeHelper;
import net.opengrabeso.glg2d.impl.SimpleOrTesselatingVisitor;
import net.opengrabeso.glg2d.GLGraphics2D;

public class GL2ShapeDrawer extends AbstractShapeHelper {
  protected GL2 gl;

  protected FillSimpleConvexPolygonVisitor simpleFillVisitor;
  protected SimpleOrTesselatingVisitor complexFillVisitor;
  protected LineDrawingVisitor simpleStrokeVisitor;
  protected FastLineVisitor fastLineVisitor;

  public GL2ShapeDrawer() {
    simpleFillVisitor = new FillSimpleConvexPolygonVisitor();
    complexFillVisitor = new SimpleOrTesselatingVisitor(simpleFillVisitor, new GL2TesselatorVisitor());
    simpleStrokeVisitor = new LineDrawingVisitor();
    fastLineVisitor = new FastLineVisitor();
  }

  @Override
  public void setG2D(GLGraphics2D g2d) {
    super.setG2D(g2d);
    GL gl = g2d.getGL();
    simpleFillVisitor.setGLContext(gl);
    complexFillVisitor.setGLContext(gl);
    simpleStrokeVisitor.setGLContext(gl);
    fastLineVisitor.setGLContext(gl);

    this.gl = gl.getGL2();
  }

  @Override
  public void setHint(Key key, Object value) {
    super.setHint(key, value);

    if (key == RenderingHints.KEY_ANTIALIASING) {
      if (value == RenderingHints.VALUE_ANTIALIAS_ON) {
        gl.glEnable(gl.GL_MULTISAMPLE());
      } else {
        gl.glDisable(gl.GL_MULTISAMPLE());
      }
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
