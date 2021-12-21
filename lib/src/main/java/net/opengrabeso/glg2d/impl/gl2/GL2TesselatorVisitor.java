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


import com.github.opengrabeso.jaagl.GL;
import com.github.opengrabeso.jaagl.GL2;

import net.opengrabeso.glg2d.GLGraphics2D;
import net.opengrabeso.glg2d.impl.AbstractTesselatorVisitor;

public class GL2TesselatorVisitor extends AbstractTesselatorVisitor {
    protected GL2 gl;
    protected GLGraphics2D glg2d;

    @Override
    public GLGraphics2D getGLG2D() {
        return glg2d;
    }

    @Override
    public void setGLContext(GL context, GLGraphics2D g2d) {
        gl = context.getGL2();
    }

    @Override
    protected void endTess() {
        vBuffer.drawBuffer(gl, drawMode);
    }
}
