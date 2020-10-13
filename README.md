# GLG2D

GLG2D is an effort to translate Graphics2D calls directly into OpenGL calls
and accelerate the Java2D drawing functionality. 

Find more information on http://opengrabeso.github.com/glg2d/

Use cases:
 * OpenGL HTML rendering using https://github.com/OpenGrabeso/flyingsaucer 
 * use as a drop-in replacement for a JPanel and all Swing children will be
    accelerated
 * draw Swing components in an GLCanvas in your existing application

This library is licensed under the Apache 2.0 license and JOGL is licensed and
distributed separately.

### Fork

Forked from http://brandonborkholder.github.com/glg2d/

This version adds following features:

- fix issues preventing use of the [Flying Saucer library](https://github.com/OpenGrabeso/flyingsaucer)
- use GL3 shader based text-renderer (allows running on a core profile)
- allow using JOGL or LWJGL 

### How to build

This project uses maven, run mvn package to build the jar in the ./target/ dir
or add the following to your pom.xml

### How to use

#### JAAGL

The library uses OpenGL via the [Jaagl abstraction layer](https://github.com/OpenGrabeso/jaagl), therefore the same
library can be used with both LWJGL and JOGL. As typical applications choose one or the other, the libraries
are linked as `provided` so that they are not required to be present both.

If necessary, it should be easy to provide Jaggl implementation for other platform / API, e.g. LWJGL OpenGL ES bindings.

At the time of writing the library was tested against JOGL 2.3.2 and LWJGL 3.2.3 (see pom.xml for up to date information).  

#### POM.XML 

The project is published at GitHub packages, add following to your pom.xml:

```
<repository>
  <id>github</id>
  <name>GitHub OpenGrabeso Apache Maven Packages</name>
  <url>https://maven.pkg.github.com/OpenGrabeso/_</url>
</repository>

<dependency>
 <groupId>net.opengrabeso.glg2d</groupId>
 <artifactId>glg2d-parent</artifactId>
 <version>${glg2d.version}</version>
</dependency>
```

Be sure to add JOGL or LWJGL dependency as well.
