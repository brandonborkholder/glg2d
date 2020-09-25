uniform mat4 u_transform;

in vec2 a_vertCoord;

void main() {
  gl_Position = u_transform * vec4(a_vertCoord, 0, 1);
}