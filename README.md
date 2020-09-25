# GLG2D

GLG2D is an effort to translate Graphics2D calls directly into OpenGL calls
and accelerate the Java2D drawing functionality.  The existing OpenGL pipeline
in the Oracle JVM is minimal at best and doesn't use higher-level OpenGL
primitives, like GL_POLYGON and GLU tesselation that make drawing in OpenGL so
fast.

Find more information on http://opengrabeso.github.com/glg2d/

Use cases:
 * use as a drop-in replacement for a JPanel and all Swing children will be
    accelerated
 * draw Swing components in an GLCanvas in your existing application

This library is licensed under the Apache 2.0 license and JOGL is licensed and
distributed separately.

### Fork

Forked from http://brandonborkholder.github.com/glg2d/

This version adds following features:

- fix issues preventing use in the [Flying Saucer library](https://github.com/OpenGrabeso/flyingsaucer)
- use GL3 shader based text-renderer
- allow using JOGL or LWJGL 

### How to build

This project uses maven, run mvn package to build the jar in the ./target/ dir
or add the following to your pom.xml

### How to use
The project is published at GitHub packages, add following to your pom.xml:

```
<repository>
  <id>github</id>
  <name>GitHub OpenGrabeso Apache Maven Packages</name>
  <url>https://maven.pkg.github.com/OpenGrabeso/_</url>
</repository>

<dependency>
 <groupId>net.opengrabeso.glg2d</groupId>
 <artifactId>glg2d</artifactId>
 <version>${glg2d.version}</version>
</dependency>
```