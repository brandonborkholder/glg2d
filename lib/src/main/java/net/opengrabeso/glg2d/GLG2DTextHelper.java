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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.text.AttributedCharacterIterator;

public interface GLG2DTextHelper extends G2DDrawingHelper {
    void setFont(Font font);

    Font getFont();

    FontMetrics getFontMetrics(Font font);

    FontRenderContext getFontRenderContext();

    void drawString(AttributedCharacterIterator iterator, int x, int y);

    void drawString(AttributedCharacterIterator iterator, float x, float y);

    void drawString(String string, float x, float y);

    void drawString(String string, int x, int y);
}