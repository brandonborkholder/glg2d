#version 130
uniform mat4 u_transform;

attribute vec2 a_vertCoord;

void main() {
  gl_Position = u_transform * vec4(a_vertCoord, 0, 1);
}