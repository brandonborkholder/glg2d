#version 120
#extension GL_EXT_geometry_shader4 : enable

uniform mat4 u_transform;
uniform int u_joinType;
uniform float u_miterLimit;
uniform float u_lineWidth;

in vec2 position[];

vec2 getLineOffsetVec(vec2, vec2);
float cross2(vec2 first, vec2 second);
void joinBevel();
void joinMiter();
void joinRound();
void emit(vec2);

void main() {
  if (u_joinType == 0) {
    joinMiter();
  } else if (u_joinType == 1) {
    joinRound();
  } else if (u_joinType == 2) {
    joinBevel();
  }

  EndPrimitive();
}

void joinMiter() {
  vec2 first = position[0];
  vec2 second = position[1];
  vec2 third = position[2];

  vec2 offset1 = getLineOffsetVec(first, second);
  vec2 offset2 = getLineOffsetVec(second, third);
  vec2 v1 = normalize(second - first);
  vec2 v2 = normalize(second - third);
  vec2 p1, p2;
  vec2 miterCorner1, miterCorner2;
  float t;

  p1 = second + offset1;
  p2 = second + offset2;

  t = cross2(p2 - p1, v2) / cross2(v1, v2);
  miterCorner1 = second + offset1 + t * v1;

  p1 = second - offset1;
  p2 = second - offset2;

  t = cross2(p2 - p1, v2) / cross2(v1, v2);
  miterCorner2 = second - offset1 + t * v1;

//  if (distance(miterCorner1, miterCorner2) / u_lineWidth <= u_miterLimit) {
    emit(first + offset1);
    emit(first - offset1);

    emit(miterCorner1);
    emit(miterCorner2);

    emit(second + offset2);
    emit(second - offset2);
//  } else {
//    joinBevel();
//  }
}

void joinRound() {
}

void joinBevel() {
  vec2 first = position[0];
  vec2 second = position[1];
  vec2 third = position[2];

  vec2 offset = getLineOffsetVec(first, second);

  emit(first + offset);
  emit(first - offset);
  emit(second + offset);
  emit(second - offset);

  offset = getLineOffsetVec(second, third);

  emit(second + offset);
  emit(second - offset);
}

float cross2(vec2 first, vec2 second) {
  return first.x * second.y - first.y * second.x;
}

vec2 getLineOffsetVec(vec2 first, vec2 second) {
  vec2 diff = second - first;
  // rotate inline
  vec2 offset = vec2(diff.y, -diff.x);

  offset = normalize(offset) * u_lineWidth / 2.0;

  return offset;
}

void emit(vec2 pt) {
  gl_Position = u_transform * vec4(pt, 0, 1);
  EmitVertex();
}
