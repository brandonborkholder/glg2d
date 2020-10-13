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

import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;

/**
 * Rendering hints for the GLG2D library that customize the behavior.
 */
public class GLG2DRenderingHints {
  private static int keyId = 384739478;

  /**
   * Never clear the texture cache.
   */
  public static final Object VALUE_CLEAR_TEXTURES_CACHE_NEVER = new Object();

  /**
   * Clear the texture cache before each paint. This allows images to be cached
   * within a paint cycle.
   */
  public static final Object VALUE_CLEAR_TEXTURES_CACHE_EACH_PAINT = new Object();

  /**
   * Use the default texture cache policy.
   */
  public static final Object VALUE_CLEAR_TEXTURES_CACHE_DEFAULT = VALUE_CLEAR_TEXTURES_CACHE_NEVER;

  /**
   * Specifies when to clear the texture cache. Each image to be painted must be
   * turned into a texture and then the texture is re-used whenever that image
   * is seen. Values can be one of
   * 
   * <ul>
   * <li>{@link #VALUE_CLEAR_TEXTURES_CACHE_DEFAULT}</li>
   * <li>{@link #VALUE_CLEAR_TEXTURES_CACHE_NEVER}</li>
   * <li>{@link #VALUE_CLEAR_TEXTURES_CACHE_EACH_PAINT}</li>
   * <li>any integer for the maximum size of the cache</li>
   * </ul>
   */
  public static final Key KEY_CLEAR_TEXTURES_CACHE = new RenderingHints.Key(keyId++) {
    public boolean isCompatibleValue(Object val) {
      return val == VALUE_CLEAR_TEXTURES_CACHE_DEFAULT ||
          val == VALUE_CLEAR_TEXTURES_CACHE_EACH_PAINT ||
          val == VALUE_CLEAR_TEXTURES_CACHE_NEVER ||
          val instanceof Integer;
    }
  };
}
