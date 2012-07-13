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

import java.awt.geom.AffineTransform;

/**
 * This class implements a Uniform Buffer Object on OpenGL ES 2.0 compatible
 * hardware.
 */
public class UniformBufferObject {
  public ColorHook colorHook;
  public TransformHook transformHook;

  public interface ColorHook {
    float[] getRGBA();

    float getAlpha();
  }

  public interface TransformHook {
    float[] getGLMatrixData();

    float[] getGLMatrixData(AffineTransform concat);
  }
}
