#version 120
#extension GL_EXT_geometry_shader4 : enable

uniform mat4 u_transform;
uniform int u_joinType;
uniform float u_miterLimit;
uniform float u_lineWidth;
uniform int u_drawEnd;

in vec2 position[];
in vec2 posBefore[];
in vec2 posAfter[];

vec2 getLineOffsetVec(vec2, vec2);
float cross2(vec2, vec2);
void emit(vec2);
void emitMiterCorner(vec2, vec2, vec2, int);
void emitRoundCorner(vec2, vec2, vec2, int);
void emitBevelCorner(vec2, vec2, vec2, int);
vec2 intersection(vec2, vec2, vec2, vec2);
void emitEnd(vec2, vec2, int);

void main() {
  if (u_drawEnd == 2) {
    // single line segment
    emitEnd(position[0], position[1], -1);
    emitEnd(position[0], position[1], 1);
  } else if (u_joinType == 0) {
    // join miter
    if (u_drawEnd < 1) {
      emitMiterCorner(posBefore[0], position[0], position[1], -1);
    } else {
      emitEnd(position[0], position[1], -1);
    }

    if (u_drawEnd > -1) {
      emitMiterCorner(position[0], position[1], posAfter[1], 1);
    } else {
      emitEnd(position[0], position[1], 1);
    }
  } else if (u_joinType == 1) {
    // join round
    if (u_drawEnd < 1) {
      emitRoundCorner(posBefore[0], position[0], position[1], -1);
    } else {
      emitEnd(position[0], position[1], -1);
    }

    if (u_drawEnd > -1) {
      emitRoundCorner(position[0], position[1], posAfter[1], 1);
    } else {
      emitEnd(position[0], position[1], 1);
    }
  } else if (u_joinType == 2) {
    // join bevel
    if (u_drawEnd < 1) {
      emitBevelCorner(posBefore[0], position[0], position[1], -1);
    } else {
      emitEnd(position[0], position[1], -1);
    }
    
    if (u_drawEnd > -1) {
      emitBevelCorner(position[0], position[1], posAfter[1], 1);
    } else {
      emitEnd(position[0], position[1], 1);
    }
  }

  EndPrimitive();
}

void emitEnd(in vec2 first, in vec2 second, in int direction) {
  vec2 offset = getLineOffsetVec(first, second);

  if (direction < 0) {
    emit(first + offset);
    emit(first - offset);
  } else if (0 < direction) {
    emit(second + offset);
    emit(second - offset);
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

  float theta = 3.1415926 - acos(dot(v1, v2));
  mat2 rotationMatrix;
  int i;
  int numSteps = int(floor(theta / 0.2));

  // p2 - p1 = (second + offset2) - (second + offset1) = offset2 - offset1
  float alpha = cross2(offset2 - offset1, v2) / cross2(v1, v2);
  
  if (alpha <= 0) {
    // right side is inside corner
    insidePt = second + offset1 + alpha * v1;
    rotationMatrix = mat2(cos(0.2), -sin(0.2), sin(0.2), cos(0.2));

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
    rotationMatrix = mat2(cos(0.2), sin(0.2), -sin(0.2), cos(0.2));

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
