GLG2D is an effort to translate Graphics2D calls directly into OpenGL calls
and accelerate the Java2D drawing functionality.  The existing OpenGL pipeline
in the Oracle JVM is minimal at best and doesn't use higher-level OpenGL
primitives, like GL_POLYGON and GLU tesselation that make drawing in OpenGL so
fast.  Therefore, this library depends on the JOGL 1.1.1a library and as JOGL
evolves, so will this library.

Find more information on http://brandonborkholder.github.com/joglj2d/

Use cases:
 * use as a drop-in replacement for a JPanel and all Swing children will be
    accelerated
 * draw Swing components in an GLCanvas in your existing application

This library is licensed under the Apache 2.0 license and JOGL is licensed and
distributed separately.

