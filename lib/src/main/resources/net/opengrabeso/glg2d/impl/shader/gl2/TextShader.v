#version 120

uniform mat4 MVPMatrix;
attribute vec4 MCVertex;
attribute vec2 TexCoord0;
varying vec2 Coord0;

void main() {
   gl_Position = MVPMatrix * MCVertex;
   Coord0 = TexCoord0;
}
