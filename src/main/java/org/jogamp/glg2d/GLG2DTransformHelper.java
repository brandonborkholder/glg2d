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
package org.jogamp.glg2d;

import java.awt.geom.AffineTransform;

public interface GLG2DTransformHelper extends G2DDrawingHelper {
  void translate(int x, int y);

  void translate(double tx, double ty);

  void rotate(double theta);

  void rotate(double theta, double x, double y);

  void scale(double sx, double sy);

  void shear(double shx, double shy);

  void transform(AffineTransform Tx);

  void setTransform(AffineTransform transform);

  AffineTransform getTransform();
}