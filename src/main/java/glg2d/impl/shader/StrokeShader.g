#version 120
#extension GL_EXT_geometry_shader4 : enable

uniform int u_joinType;
uniform int u_miterLimit;

in float lineWidth[];

void main() {
  vec4 offset = vec4(lineWidth[0], 0, 0, 0);
  vec4 first = gl_PositionIn[0];
  vec4 second = gl_PositionIn[1];
  vec4 third = gl_PositionIn[2];
  
  gl_Position = second - offset;
  EmitVertex();
  gl_Position = second + offset;
  EmitVertex();
  gl_Position = third - offset;
  EmitVertex();
  gl_Position = third + offset;
  EmitVertex();

  EndPrimitive();
}
