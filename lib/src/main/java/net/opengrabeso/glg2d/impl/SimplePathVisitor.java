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
package net.opengrabeso.glg2d.impl;

import net.opengrabeso.glg2d.PathVisitor;

import java.awt.geom.Point2D;

/**
 * This is a fast B&eacute;zier curve implementation. I can't use OpenGL's
 * built-in evaluators because subclasses need to do something with the points,
 * not just pass them directly to glVertex2f. This algorithm uses forward
 * differencing. Most of this is taken from <a
 * href="http://www.niksula.hut.fi/~hkankaan/Homepages/bezierfast.html"
 * >http://www.niksula.hut.fi/~hkankaan/Homepages/bezierfast.html</a>. I derived
 * the implementation for the quadratic on my own, but it's simple.
 */
public abstract class SimplePathVisitor implements PathVisitor {

    private float distance(float pointX, float pointY, float x, float y) {
        float dx = x - pointX;
        float dy = y - pointY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private Point2D.Float tPoint(float begX, float begY, float endX, float endY, float t) {
        return new Point2D.Float(
                begX + (endX - begX) * t,
                begY + (endY - begY) * t
        );
    }

    private float distance(float pointX, float pointY, float begX, float begY, float endX, float endY) {
        float px = endX - begX;
        float py = endY - begY;
        float u = ((pointX - begX) * px + (pointY - begY) * py) / ((px * px) + (py * py));
        float x = begX + u * px;
        float y = begY + u * py;

        return distance(pointX, pointY, x, y);
    }

    @Override
    public void quadTo(float[] previousVertex, float[] control) {
        assert !Double.isNaN(previousVertex[0]);
        assert !Double.isNaN(previousVertex[1]);

        assert !Double.isNaN(control[0]);
        assert !Double.isNaN(control[1]);
        assert !Double.isNaN(control[2]);
        assert !Double.isNaN(control[3]);

        // error estimation: distance of control points from the center point
        Point2D.Float midPoint = tPoint(previousVertex[0], previousVertex[1], control[2], control[3], 0.5f);
        float err = distance(control[0], control[1], midPoint.x, midPoint.y);

        int steps = Math.round(err);

        float[] p = new float[2];

        float xd, xdd, xdd_per_2;
        float yd, ydd, ydd_per_2;
        float t = 1F / steps;
        float tt = t * t;

        // x
        p[0] = previousVertex[0];
        xd = 2 * (control[0] - previousVertex[0]) * t;
        xdd_per_2 = 1 * (previousVertex[0] - 2 * control[0] + control[2]) * tt;
        xdd = xdd_per_2 + xdd_per_2;

        // y
        p[1] = previousVertex[1];
        yd = 2 * (control[1] - previousVertex[1]) * t;
        ydd_per_2 = 1 * (previousVertex[1] - 2 * control[1] + control[3]) * tt;
        ydd = ydd_per_2 + ydd_per_2;

        for (int loop = 0; loop < steps; loop++) {
            lineTo(p);

            p[0] = p[0] + xd + xdd_per_2;
            xd = xd + xdd;

            p[1] = p[1] + yd + ydd_per_2;
            yd = yd + ydd;
        }

        // use exactly the last point
        p[0] = control[2];
        p[1] = control[3];
        lineTo(p);
    }

    @Override
    public void cubicTo(float[] previousVertex, float[] control) {
        float[] p = new float[2];

        // error estimation: distance of control points from the point on the straight line
        Point2D.Float midPoint1 = tPoint(previousVertex[0], previousVertex[1], control[4], control[5], 1.0f/3);
        Point2D.Float midPoint2 = tPoint(previousVertex[0], previousVertex[1], control[4], control[5], 2.0f/3);

        // error estimation: distance of control points from the straight line
        float err1 = distance(control[0], control[1], midPoint1.x, midPoint1.y);
        float err2 = distance(control[2], control[3], midPoint2.x, midPoint2.y);

        float err = Math.max(err1, err2);

        int steps = Math.round(err);

        float xd, xdd, xddd, xdd_per_2, xddd_per_2, xddd_per_6;
        float yd, ydd, yddd, ydd_per_2, yddd_per_2, yddd_per_6;
        float t = 1F / steps;
        float tt = t * t;

        // x
        p[0] = previousVertex[0];
        xd = 3 * (control[0] - previousVertex[0]) * t;
        xdd_per_2 = 3 * (previousVertex[0] - 2 * control[0] + control[2]) * tt;
        xddd_per_2 = 3 * (3 * (control[0] - control[2]) + control[4] - previousVertex[0]) * tt * t;

        xddd = xddd_per_2 + xddd_per_2;
        xdd = xdd_per_2 + xdd_per_2;
        xddd_per_6 = xddd_per_2 / 3;

        // y
        p[1] = previousVertex[1];
        yd = 3 * (control[1] - previousVertex[1]) * t;
        ydd_per_2 = 3 * (previousVertex[1] - 2 * control[1] + control[3]) * tt;
        yddd_per_2 = 3 * (3 * (control[1] - control[3]) + control[5] - previousVertex[1]) * tt * t;

        yddd = yddd_per_2 + yddd_per_2;
        ydd = ydd_per_2 + ydd_per_2;
        yddd_per_6 = yddd_per_2 / 3;

        for (int loop = 0; loop < steps; loop++) {
            lineTo(p);

            p[0] = p[0] + xd + xdd_per_2 + xddd_per_6;
            xd = xd + xdd + xddd_per_2;
            xdd = xdd + xddd;
            xdd_per_2 = xdd_per_2 + xddd_per_2;

            p[1] = p[1] + yd + ydd_per_2 + yddd_per_6;
            yd = yd + ydd + yddd_per_2;
            ydd = ydd + yddd;
            ydd_per_2 = ydd_per_2 + yddd_per_2;
        }

        // use exactly the last point
        p[0] = control[4];
        p[1] = control[5];
        lineTo(p);
    }
}
