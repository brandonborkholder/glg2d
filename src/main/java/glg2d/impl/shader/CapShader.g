#version 120
#extension GL_EXT_geometry_shader4 : enable

uniform mat4 u_transform;
uniform int u_capType;
uniform float u_lineWidth;

in vec2 position[];

vec2 getLineOffsetVec(vec2, vec2);
void capButt();
void capSquare();
void capRound();
void emit(vec2);

void main() {
  if (u_capType == 0) {
    capButt();
  } else if (u_capType == 1) {
    capRound();
  } else if (u_capType == 2) {
    capSquare();
  }

  EndPrimitive();
}

void capButt() {
  vec2 first = position[0];
  vec2 second = position[1];

  vec2 offset = getLineOffsetVec(first, second);
  
  emit(first + offset);
  emit(first - offset);
  emit(second + offset);
  emit(second - offset);
}

void capRound() {
  vec2 first = position[0];
  vec2 second = position[1];
  
  float stepSize = 0.2f;
  mat2 rotation = mat2(cos(stepSize), sin(stepSize), -sin(stepSize), cos(stepSize));
  int i = 0;
  int numSteps = int(floor(3.141592 / stepSize) / 2);
  
  vec2 offset = getLineOffsetVec(first, second);
  vec2 rotated_offset;

  // draw the line
  emit(first + offset);
  emit(first - offset);
  emit(second + offset);
  emit(second - offset);
  EndPrimitive();

  // draw the triangles that create the round cap
  rotated_offset = offset;
  for (i = 0; i < numSteps; i++) {
    emit(second);
    emit(second + rotated_offset);
    rotated_offset = rotation * rotated_offset;
    emit(second + rotated_offset);
    rotated_offset = rotation * rotated_offset;
  }
  
  // finish with the last one
  emit(second);
  emit(second - offset);
}

void capSquare() {
  vec2 first = position[0];
  vec2 second = position[1];

  vec2 offset = getLineOffsetVec(first, second);
  
  vec2 cap = normalize(second - first) * u_lineWidth / 2.0;

  emit(first + offset);
  emit(first - offset);
  emit(second + offset + cap);
  emit(second - offset + cap);
}

vec2 getLineOffsetVec(vec2 first, vec2 second) {
  vec2 diff = second - first;
  // inline rotation to perpendicular
  vec2 offset = vec2(diff.y, -diff.x);

  offset = normalize(offset) * u_lineWidth / 2.0;

  return offset;
}

void emit(vec2 pt) {
  gl_Position = u_transform * vec4(pt, 0, 1);
  EmitVertex();
}
