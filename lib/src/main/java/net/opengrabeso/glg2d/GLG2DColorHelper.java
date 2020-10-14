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

import java.awt.Color;
import java.awt.Composite;
import java.awt.Paint;

public interface GLG2DColorHelper extends G2DDrawingHelper {
    void setComposite(Composite comp);

    Composite getComposite();

    void setPaint(Paint paint);

    Paint getPaint();

    void setColor(Color c);

    Color getColor();

    void setColorNoRespectComposite(Color c);

    void setColorRespectComposite(Color c);

    void setBackground(Color color);

    Color getBackground();

    void setPaintMode();

    void setXORMode(Color c);

    void copyArea(int x, int y, int width, int height, int dx, int dy);
}