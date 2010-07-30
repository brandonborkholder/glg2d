/**************************************************************************
   Copyright 2010 Brandon Borkholder

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 ***************************************************************************/

package joglg2d;

public abstract class SimplePathVisitor implements PathVisitor {
  public static final float CURVE_STEP_SIZE = 0.1F;

  protected float stepSize = CURVE_STEP_SIZE;

  @Override
  public void quadTo(float[] previousVertex, float[] control) {
    float[] p = new float[2];
    float i = 0;
    for (; i <= 1; i += stepSize) {
      float j = 1 - i;
      p[0] = j * j * previousVertex[0] + 2 * j * i * control[0] + i * i * control[2];
      p[1] = j * j * previousVertex[1] + 2 * j * i * control[1] + i * i * control[3];
      lineTo(p);
    }

    if (i != 1) {
      p[0] = control[2];
      p[1] = control[3];
      lineTo(p);
    }
  }

  @Override
  public void cubicTo(float[] previousVertex, float[] control) {
    float[] p = new float[2];
    float i = 0;
    for (; i <= 1; i += stepSize) {
      float j = 1 - i;
      p[0] = j * j * j * previousVertex[0] + 3 * j * j * i * control[0] + 3 * j * i * i * control[2] + i * i * i * control[4];
      p[1] = j * j * j * previousVertex[1] + 3 * j * j * i * control[1] + 3 * j * i * i * control[3] + i * i * i * control[5];
      lineTo(p);
    }

    if (i != 1) {
      p[0] = control[4];
      p[1] = control[5];
      lineTo(p);
    }
  }
}
