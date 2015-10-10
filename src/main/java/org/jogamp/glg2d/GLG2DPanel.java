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
package org.jogamp.glg2d;

import java.awt.Component;
import java.awt.Graphics;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.awt.GLCanvas;
import javax.swing.JComponent;

import org.jogamp.glg2d.event.AWTMouseEventTranslator;

/**
 * This wraps an AWT component hierarchy and paints it using OpenGL. The
 * drawable component can be any JComponent.
 *
 * <p>
 * If painting a simple scene using the
 * {@link JComponent#paintComponents(Graphics)}, then use {@link GLG2DCanvas}.
 * </p>
 *
 * <p>
 * GL drawing can be enabled or disabled using the {@code setGLDrawing(boolean)}
 * method. If GL drawing is enabled, all full paint requests are intercepted and
 * the drawable component is drawn to the OpenGL canvas.
 * </p>
 */
public class GLG2DPanel extends GLG2DCanvas {
    private static final long serialVersionUID = 83442176852921790L;

    private AWTMouseEventTranslator mouseListener;

    public GLG2DPanel(GLCapabilities capabilities, JComponent drawableComponent) {
        super(capabilities, drawableComponent);
    }

    public GLG2DPanel(JComponent drawableComponent) {
        super(drawableComponent);
    }

    @Override
    public void setDrawableComponent(JComponent component) {
        if (mouseListener != null) {
            mouseListener = null;

            Component c = (Component) canvas;
            c.removeMouseListener(mouseListener);
            c.removeMouseMotionListener(mouseListener);
            c.removeMouseWheelListener(mouseListener);
        }

        super.setDrawableComponent(component);

        if (getDrawableComponent() != null && canvas instanceof Component) {
            mouseListener = new AWTMouseEventTranslator(getDrawableComponent());

            Component c = (Component) canvas;
            c.addMouseListener(mouseListener);
            c.addMouseMotionListener(mouseListener);
            c.addMouseWheelListener(mouseListener);
        }
    }

    @Override
    protected GLAutoDrawable createGLComponent(GLCapabilitiesImmutable capabilities, GLContext shareWith) {
        GLAutoDrawable canvas = super.createGLComponent(capabilities, shareWith);
        if (canvas instanceof GLCanvas) {
            ((GLCanvas) canvas).setEnabled(true);
        }

        return canvas;
    }
}
