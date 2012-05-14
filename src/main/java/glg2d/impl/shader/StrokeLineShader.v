uniform mat4 u_transform;

in vec2 a_vertCoord;

void main() {
  // add 0.5 so we're in the upper/right of the pixel boundary
  gl_Position = u_transform * vec4(a_vertCoord, 0, 1);
}