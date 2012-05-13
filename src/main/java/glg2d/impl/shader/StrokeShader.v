#version 130

in vec2 a_vertCoord;
in vec2 a_vertBefore;
in vec2 a_vertAfter;

out vec2 position;
out vec2 posBefore;
out vec2 posAfter;

void main() {
  position = a_vertCoord;
  posBefore = a_vertBefore;
  posAfter = a_vertAfter;
}

