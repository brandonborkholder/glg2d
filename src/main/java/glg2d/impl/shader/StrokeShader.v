#version 130

uniform mat4 u_transform;
uniform float u_lineWidth;

in vec2 a_vertCoord;

out float lineWidth;

void main() {
  vec4 width = vec4(u_lineWidth, 0, 0, 1);
  width = u_transform * width;
  lineWidth = width.x;
  gl_Position = u_transform * vec4(a_vertCoord.x, a_vertCoord.y, 0, 1);
}