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
package glg2d;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;

public interface GLG2DImageHelper extends G2DDrawingHelper {
  boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer);

  boolean drawImage(Image img, AffineTransform xform, ImageObserver observer);

  boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer);

  boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer);

  void drawImage(BufferedImage img, BufferedImageOp op, int x, int y);

  void drawImage(RenderedImage img, AffineTransform xform);

  void drawImage(RenderableImage img, AffineTransform xform);
}