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
package net.opengrabeso.glg2d.impl.shader;


import com.github.opengrabeso.jaagl.GL;

import net.opengrabeso.glg2d.GLGraphics2D;
import net.opengrabeso.glg2d.PathVisitor;

public interface ShaderPathVisitor extends PathVisitor {
    void setGLContext(GL glContext, GLGraphics2D g2D, UniformBufferObject uniforms);
}
