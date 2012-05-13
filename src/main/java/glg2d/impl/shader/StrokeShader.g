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
void emitEnd(vec2, vec2, int);

void main() {
//  if (u_joinType == 0) {
//    if (u_drawEnd < 1) {
//      emitMiterCorner(posBefore[0], position[0], position[1], -1);
//    } else {
//      emitEnd(position[0], position[1], -1);
      emitEnd(posBefore[0], position[0], 1);
//    }

//    if (u_drawEnd > -1) {
//      emitMiterCorner(position[0], position[1], posAfter[1], 1);
//    } else {
//      emitEnd(position[0], position[1], 1);
      emitEnd(position[1], posAfter[1], -1);
//    }
//  } else if (u_joinType == 1) {
//    if (u_drawEnd < 1) {
//      emitRoundCorner(position[0], position[1], position[2], 1);
//    } else {
//      emitEnd(position[1], position[2], 1);
//    }
//
//    if (u_drawEnd > -1) {
//      emitRoundCorner(position[1], position[2], position[3], -1);
//    } else {
//      emitEnd(position[1], position[2], -1);
//    }
//  } else if (u_joinType == 2) {
//    if (u_drawEnd < 1) {
//      emitBevelCorner(position[0], position[1], position[2], 1);
//    } else {
//      emitEnd(position[1], position[2], 1);
//    }
//    
//    if (u_drawEnd > -1) {
//      emitBevelCorner(position[1], position[2], position[3], -1);
//    } else {
//      emitEnd(position[1], position[2], -1);
//    }
//  }

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
    emit(miterCorner1);
    emit(miterCorner2);
//  } else {
//    emitBevelCorner(first, second, third, direction);
//  }
}

void emitRoundCorner(in vec2 first, in vec2 second, in vec2 third, in int direction) {
}

void emitBevelCorner(in vec2 first, in vec2 second, in vec2 third, in int direction) {
  vec2 offset1 = getLineOffsetVec(first, second);
  vec2 offset2 = getLineOffsetVec(second, third);
  
  vec2 bisector = offset1 + 0.5 * (offset2 - offset1);
  bisector = bisector * u_lineWidth / 2.0;
  
  vec2 rightPt = mix(second + offset1, second + offset2, 0.5);
  vec2 leftPt = mix(second - offset1, second - offset2, 0.5);

  if (dot(bisector, rightPt) >= 0) {
    // left is the outside corner
    
    if (direction < 0) {
      // coming into the corner
      emit(bisector);
      emit(second - offset1);
    }
    
    emit(bisector);
    emit(leftPt);
    
    if (direction > 0) {
      // going out of the corner
      emit(bisector);
      emit(second - offset2);
    }
  } else {
    // right is outside corner
    
    if (direction < 0) {
      emit(second + offset1);
      emit(bisector);
    }
    
    emit(rightPt);
    emit(bisector);
    
    if (direction > 0) {
      emit(second + offset2);
      emit(bisector);
    }
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
