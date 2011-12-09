/**************************************************************************
   Copyright 2011 Brandon Borkholder

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

package glg2d;

import java.awt.BasicStroke;
import java.awt.geom.PathIterator;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;

/**
 * Tesselating is expensive. This is a simple workaround to check if we can just
 * draw the convex polygon without tesselating. At each corner, we have to check
 * the sign of the z-component of the cross-product. If it's the same all the
 * way around, we know that every turn went the same direction. That's
 * necessary, but not sufficient since we might still have self-intersections. I
 * haven't thought of a fast way to check that, yet.
 *
 * <p>
 * So for now this just checks every corner and if it has the same sign, we
 * assume the polygon is convex. Once we get to the end, we draw it. If it's not
 * convex, then we fall back to tesselating it.
 * </p>
 * <p>
 * There are many places where we could fail being a simple convex polygon and
 * then have to fail over to the tesselator. As soon as we fail over we need to
 * catch the tesselator up to the current position and then use the tesselator
 * from then on. For that reason, this class is a little messy.
 * </p>
 */
public class PolygonOrTesselatingVisitor extends SimplePathVisitor {
  protected GL gl;

  /**
   * This buffer is used to store points for the simple polygon, until we find
   * out it's not simple. Then we push all this data to the tesselator and
   * ignore the buffer.
   */
  protected VertexBuffer buffer = VertexBuffer.getSharedBuffer();

  /**
   * This is the buffer of vertices we'll use to test the corner.
   */
  protected float[] previousVertices;
  protected int numberOfPreviousVertices;

  /**
   * All corners must have the same sign.
   */
  protected int sign;

  /**
   * The flag to indicate if we currently believe this polygon to be simple and
   * convex.
   */
  protected boolean isConvexSoFar;

  /**
   * The flag to indicate if we are on our first segment (move-to). If we have
   * multiple move-to's, then we need to tesselate.
   */
  protected boolean firstSegment;

  protected PathVisitor tesselatorFallback;

  @Override
  public void setGLContext(GL context) {
    gl = context;
  }

  @Override
  public void setStroke(BasicStroke stroke) {
    // this is only used to fill, no need to consider stroke
  }

  @Override
  public void beginPoly(int windingRule) {
    isConvexSoFar = true;
    firstSegment = true;
    sign = 0;

    assert windingRule == PathIterator.WIND_NON_ZERO : "Invalid assumption";
  }

  @Override
  public void moveTo(float[] vertex) {
    if (firstSegment) {
      firstSegment = false;
    } else if (isConvexSoFar) {
      setUseTesselator(true);
    }

    if (isConvexSoFar) {
      numberOfPreviousVertices = 1;
      previousVertices = new float[] { vertex[0], vertex[1], 0, 0 };

      buffer.clear();
      buffer.addVertex(vertex, 0, 1);
    } else {
      tesselatorFallback.moveTo(vertex);
    }
  }

  @Override
  public void lineTo(float[] vertex) {
    if (isConvexSoFar) {
      buffer.addVertex(vertex, 0, 1);

      if (!isValidCorner(vertex)) {
        setUseTesselator(false);
      }
    } else {
      tesselatorFallback.lineTo(vertex);
    }
  }

  /**
   * Returns true if the corner is correct, using the new vertex and the buffer
   * of previous vertices. This always updates the buffer of previous vertices.
   */
  protected boolean isValidCorner(float[] vertex) {
    if (numberOfPreviousVertices >= 2) {
      int currentSign = crossSign(vertex);
      if (sign == 0) {
        sign = currentSign;

        // allow for currentSign = 0, in which case we don't care
      } else if (currentSign * sign == -1) {
        return false;
      }
    }

    numberOfPreviousVertices++;
    previousVertices[2] = previousVertices[0];
    previousVertices[3] = previousVertices[1];
    previousVertices[0] = vertex[0];
    previousVertices[1] = vertex[1];

    return true;
  }

  /**
   * Returns the sign of the z-component of the cross-product.
   */
  protected int crossSign(float[] next) {
    float diff1 = previousVertices[2] - previousVertices[0];
    float diff2 = previousVertices[3] - previousVertices[1];
    float diff3 = next[0] - previousVertices[0];
    float diff4 = next[1] - previousVertices[1];

    float value = diff1 * diff4 - diff2 * diff3;
    if (value == 0) {
      return 0;
    } else if (value < 0) {
      return -1;
    } else {
      return 1;
    }
  }

  @Override
  public void closeLine() {
    if (isConvexSoFar) {
      /*
       * If we're convex so far, we need to finish out all the corners to make
       * sure everything is kosher.
       */
      FloatBuffer buf = buffer.getBuffer();
      float[] vertex = new float[2];
      int position = buf.position();

      buf.rewind();
      buf.get(vertex);

      boolean good = false;
      if (isValidCorner(vertex)) {
        buf.get(vertex);
        if (isValidCorner(vertex)) {
          good = true;
        }
      }

      buf.position(position);

      if (!good) {
        setUseTesselator(true);
      }
    } else {
      tesselatorFallback.closeLine();
    }
  }

  @Override
  public void endPoly() {
    if (isConvexSoFar) {
      // we got through all the checks, draw it fast
      buffer.drawBuffer(gl, GL.GL_POLYGON);
    } else {
      tesselatorFallback.endPoly();
    }
  }

  /**
   * Sets the state to start using the tesselator. This will catch the
   * tesselator up to the current position and then set {@code isConvexSoFar} to
   * false so we can start using the tesselator exclusively.
   *
   * If {@code doClose} is true, then we will also close the line when we update
   * the tesselator. This is for when we realized it's not a simple poly after
   * we already finished the first path.
   */
  protected void setUseTesselator(boolean doClose) {
    isConvexSoFar = false;

    if (tesselatorFallback == null) {
      tesselatorFallback = new TesselatorVisitor();
      tesselatorFallback.setGLContext(gl);
    }

    // we made this assumption
    tesselatorFallback.beginPoly(PathIterator.WIND_NON_ZERO);
    drawToTesselator(doClose);
  }

  protected void drawToTesselator(boolean doClose) {
    /*
     * Need to be careful that the tesselator functionality doesn't use the
     * shared VertexBuffer and clobber it.
     */
    FloatBuffer buf = buffer.getBuffer();
    int position = buf.position();
    buf.limit(position);
    buf.rewind();

    float[] vertex = new float[2];
    buf.get(vertex);

    tesselatorFallback.moveTo(vertex);
    while (buf.hasRemaining()) {
      buf.get(vertex);
      tesselatorFallback.lineTo(vertex);
    }

    if (doClose) {
      tesselatorFallback.closeLine();
    }

    // put everything back the way it was
    buf.limit(buf.capacity());
    buf.position(position);
  }
}
