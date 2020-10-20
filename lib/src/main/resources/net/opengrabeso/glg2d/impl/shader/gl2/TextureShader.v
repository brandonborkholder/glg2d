#version 110
uniform mat4 u_transform;

attribute vec2 a_vertCoord;
attribute vec2 a_texCoord;

varying vec2 v_texCoord;

void main() {
  gl_Position = u_transform * vec4(a_vertCoord.x, a_vertCoord.y, 0, 1);
  v_texCoord = a_texCoord;
}