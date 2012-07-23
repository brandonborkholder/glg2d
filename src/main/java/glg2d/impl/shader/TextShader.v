uniform mat4 u_transform;
uniform float u_xoffset;
uniform float u_yoffset;

in vec2 a_vertCoord;

void main() {
  gl_Position = u_transform * vec4(a_vertCoord.x + u_xoffset, a_vertCoord.y + u_yoffset, 0, 1);
}