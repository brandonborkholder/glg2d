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
package glg2d.impl.shader;

import java.awt.BasicStroke;
import java.nio.FloatBuffer;

import javax.media.opengl.GL2ES2;

public interface ShapeShaderPipeline extends ShaderPipeline {
  void setGLTransform(GL2ES2 gl, FloatBuffer glMatrixBuffer);

  void setColor(GL2ES2 gl, float[] rgba);

  void setStroke(GL2ES2 gl, BasicStroke stroke);

  void draw(GL2ES2 gl, FloatBuffer buffer);
}
