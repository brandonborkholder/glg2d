#version 120
#extension GL_EXT_geometry_shader4 : enable

#define DRAW_END_NONE 0
#define DRAW_END_FIRST -1
#define DRAW_END_LAST 1
#define DRAW_END_BOTH 2

#define JOIN_MITER 0
#define JOIN_ROUND 1
#define JOIN_BEVEL 2

#define CAP_BUTT 0
#define CAP_ROUND 1
#define CAP_SQUARE 2

#define THETA_STEP 0.2
#define COS_THETA_STEP cos(THETA_STEP)
#define SIN_THETA_STEP sin(THETA_STEP)

#define PI 3.141592653

uniform mat4 u_transform;
uniform int u_joinType;
uniform int u_capType;
uniform float u_miterLimit;
uniform float u_lineWidth;
uniform int u_drawEnd;

in vec2 position[];
in vec2 posBefore[];
in vec2 posAfter[];

vec2 getLineOffsetVec(vec2, vec2);
float cross2(vec2, vec2);
void emit(vec2);
void emitCorner(vec2, vec2, vec2, int);
void emitMiterCorner(vec2, vec2, vec2, int);
void emitRoundCorner(vec2, vec2, vec2, int);
void emitBevelCorner(vec2, vec2, vec2, int);
void emitCap(vec2, vec2, int);
void emitButtCap(vec2, vec2, int);
void emitRoundCap(vec2, vec2, int);
void emitSquareCap(vec2, vec2, int);
vec2 intersection(vec2, vec2, vec2, vec2);

void main() {
  if (u_drawEnd == DRAW_END_BOTH) {
    // single line segment
    emitCap(position[0], position[1], DRAW_END_FIRST);
    emitCap(position[0], position[1], DRAW_END_LAST);
  } else {
    if (u_drawEnd == DRAW_END_FIRST) {
      emitCap(position[0], position[1], DRAW_END_FIRST);
    } else {
      emitCorner(posBefore[0], position[0], position[1], DRAW_END_FIRST);
    }

    if (u_drawEnd == DRAW_END_LAST) {
      emitCap(position[0], position[1], DRAW_END_LAST);
    } else {
      emitCorner(position[0], position[1], posAfter[1], DRAW_END_LAST);
    }
  }

  EndPrimitive();
}

void emitCap(in vec2 first, in vec2 second, in int direction) {
  if (u_capType == CAP_BUTT) {
    emitButtCap(first, second, direction);
  } else if (u_capType == CAP_ROUND) {
    emitRoundCap(first, second, direction);
  } else {
    emitSquareCap(first, second, direction);
  }
}

void emitButtCap(in vec2 first, in vec2 second, in int direction) {
  vec2 offset = getLineOffsetVec(first, second);

  if (direction < 0) {
    emit(first + offset);
    emit(first - offset);
  } else if (0 < direction) {
    emit(second + offset);
    emit(second - offset);
  }
}

void emitRoundCap(in vec2 first, in vec2 second, in int direction) {
  // Instead of doing a triangle-fan around the cap, we're going to jump back
  // and forth from the tip toward the body of the line.

  vec2 extended = normalize(second - first) * u_lineWidth / 2;
  float theta = PI / 2.0;
  mat2 rotationMatrixRight = mat2(COS_THETA_STEP, SIN_THETA_STEP, -SIN_THETA_STEP, COS_THETA_STEP);
  mat2 rotationMatrixLeft = mat2(COS_THETA_STEP, -SIN_THETA_STEP, SIN_THETA_STEP, COS_THETA_STEP);

  int i;
  int numSteps = int(floor(theta / 0.2));

  vec2 offsetRight;
  vec2 offsetLeft;
  vec2 pt;

  if (direction == DRAW_END_FIRST) {
    offsetRight = -extended;
    offsetLeft = -extended;
    pt = first;
  } else {
    offsetRight = getLineOffsetVec(first, second);
    offsetLeft = -offsetRight;
    pt = second;
  }

  for (i = 0; i < numSteps; i++) {
    emit(pt + offsetRight);
    emit(pt + offsetLeft);

    offsetRight = rotationMatrixRight * offsetRight;
    offsetLeft = rotationMatrixLeft * offsetLeft;
  }

  if (direction == DRAW_END_FIRST) {
    offsetRight = getLineOffsetVec(first, second);

    emit(pt + offsetRight);
    emit(pt - offsetRight);
  } else {
    emit(pt + extended);
  }
}

void emitSquareCap(in vec2 first, in vec2 second, in int direction) {
  vec2 offset = getLineOffsetVec(first, second);
  // point in direction of first to second
  vec2 extended = normalize(second - first) * u_lineWidth / 2.0;

  if (direction == DRAW_END_FIRST) {
    emit(first + offset - extended);
    emit(first - offset - extended);
  } else {
    emit(second + offset + extended);
    emit(second - offset + extended);
  }
}

void emitCorner(in vec2 first, in vec2 second, in vec2 third, in int direction) {
  if (u_joinType == JOIN_MITER) {
    emitMiterCorner(first, second, third, direction);
  } else if (u_joinType == JOIN_ROUND) {
    emitRoundCorner(first, second, third, direction);
  } else {
    emitBevelCorner(first, second, third, direction);
  }
}

void emitMiterCorner(in vec2 first, in vec2 second, in vec2 third, in int direction) {
  vec2 offset1 = getLineOffsetVec(first, second);
  vec2 offset2 = getLineOffsetVec(second, third);
  vec2 v1 = normalize(second - first);
  vec2 v2 = normalize(second - third);
  vec2 miterCorner1, miterCorner2;
  float alpha;
  
  // p2 - p1 = (second + offset2) - (second + offset1) = offset2 - offset1
  alpha = cross2(offset2 - offset1, v1) / cross2(v1, v2);
  miterCorner1 = second + offset1 + alpha * v1;

  // p2 - p1 = (second - offset2) - (second - offset1) = -(offset2 - offset1)
  // => we get the negative alpha from above
  miterCorner2 = second - offset1 - alpha * v1;

  if (distance(miterCorner1, miterCorner2) / u_lineWidth <= u_miterLimit) {
    emit(miterCorner1);
    emit(miterCorner2);
  } else {
    emitBevelCorner(first, second, third, direction);
  }
}

vec2 intersection(in vec2 p1, in vec2 v1, in vec2 p2, in vec2 v2) {
  float t = cross2(p2 - p1, v2) / cross2(v1, v2);
  return p1 + t * v1;
}

void emitRoundCorner(in vec2 first, in vec2 second, in vec2 third, in int direction) {
  vec2 offset1 = getLineOffsetVec(first, second);
  vec2 offset2 = getLineOffsetVec(second, third);
  vec2 v1 = normalize(second - first);
  vec2 v2 = normalize(second - third);
  vec2 insidePt;

  float theta = PI - acos(dot(v1, v2));
  mat2 rotationMatrix;
  int i;
  int numSteps = int(floor(theta / 0.2));

  // p2 - p1 = (second + offset2) - (second + offset1) = offset2 - offset1
  float alpha = cross2(offset2 - offset1, v2) / cross2(v1, v2);
  
  if (alpha <= 0) {
    // right side is inside corner
    insidePt = second + offset1 + alpha * v1;
    rotationMatrix = mat2(COS_THETA_STEP, -SIN_THETA_STEP, SIN_THETA_STEP, COS_THETA_STEP);

    emit(insidePt);
    emit(second - offset1);
    
    for (i = 0; i < numSteps; i++) {
      offset1 = rotationMatrix * offset1;
      
      emit(insidePt);
      emit(second - offset1);
    }

    emit(insidePt);
    emit(second - offset2);
  } else {
    // left side is inside corner
    alpha = -alpha;
    
    insidePt = second - offset1 + alpha * v1;
    rotationMatrix = mat2(COS_THETA_STEP, SIN_THETA_STEP, -SIN_THETA_STEP, COS_THETA_STEP);

    emit(second + offset1);
    emit(insidePt);

    for (i = 0; i < numSteps; i++) {
      offset1 = rotationMatrix * offset1;
      
      emit(second + offset1);
      emit(insidePt);
    }

    emit(second + offset2);
    emit(insidePt);
  }
}

void emitBevelCorner(in vec2 first, in vec2 second, in vec2 third, in int direction) {
  vec2 offset1 = getLineOffsetVec(first, second);
  vec2 offset2 = getLineOffsetVec(second, third);
  vec2 v1 = normalize(second - first);
  vec2 v2 = normalize(second - third);
  vec2 insidePt;

  // p2 - p1 = (second + offset2) - (second + offset1) = offset2 - offset1
  float alpha = cross2(offset2 - offset1, v2) / cross2(v1, v2);

  if (alpha <= 0) {
    // right side is inside corner
    insidePt = second + offset1 + alpha * v1;
    
    emit(insidePt);
    emit(second - offset1);
    emit(insidePt);
    emit(second - offset2);
  } else {
    // p2 - p1 = (second - offset2) - (second - offset1) = -(offset2 - offset1)
    // => we get -alpha from above
    insidePt = second - offset1 - alpha * v1;

    emit(second + offset1);
    emit(insidePt);
    emit(second + offset2);
    emit(insidePt);
  }
}

float cross2(in vec2 first, in vec2 second) {
  return first.x * second.y - first.y * second.x;
}

// gets perpendicular vector (to the right) of the line going from first to second
vec2 getLineOffsetVec(in vec2 first, in vec2 second) {
  vec2 diff = normalize(second - first);
  vec2 offset = vec2(diff.y, -diff.x);
  // inline rotation 90°

  offset = offset * u_lineWidth / 2.0;

  return offset;
}

void emit(in vec2 pt) {
  gl_Position = u_transform * vec4(pt, 0, 1);
  EmitVertex();
}
