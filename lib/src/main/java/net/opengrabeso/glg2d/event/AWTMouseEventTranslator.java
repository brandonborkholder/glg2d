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
package net.opengrabeso.glg2d.event;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class AWTMouseEventTranslator extends MouseEventTranslator implements MouseListener, MouseMotionListener, MouseWheelListener {
    public AWTMouseEventTranslator(Component target) {
        super(target);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        translateMouseWheelEvent(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        translateMouseEvent(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        translateMouseEvent(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        translateMouseEvent(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        translateMouseEvent(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        translateMouseEvent(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        translateMouseEvent(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        translateMouseEvent(e);
    }

    protected void translateMouseEvent(MouseEvent e) {
        publishMouseEvent(e.getID(), e.getWhen(), e.getModifiers(), e.getClickCount(), e.getButton(), e.getPoint());
    }

    protected void translateMouseWheelEvent(MouseWheelEvent e) {
        publishMouseWheelEvent(e.getID(), e.getWhen(), e.getModifiers(), e.getWheelRotation(), e.getPoint());
    }
}
