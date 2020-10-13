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
package net.opengrabeso.glg2d;

import java.awt.Shape;
import java.awt.Stroke;

public interface GLG2DShapeHelper extends G2DDrawingHelper {
  void setStroke(Stroke stroke);

  Stroke getStroke();

  void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight, boolean fill);

  void drawRect(int x, int y, int width, int height, boolean fill);

  void drawLine(int x1, int y1, int x2, int y2);

  void drawOval(int x, int y, int width, int height, boolean fill);

  void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle, boolean fill);

  void drawPolyline(int[] xPoints, int[] yPoints, int nPoints);

  void drawPolygon(int[] xPoints, int[] yPoints, int nPoints, boolean fill);

  void draw(Shape shape);

  void fill(Shape shape);
}