#version 130

uniform sampler2D Texture;
uniform vec4 Color=vec4(1,1,1,1);
varying vec2 Coord0;

void main() {
   float sample;
   sample = texture(Texture,Coord0).r;
   gl_FragColor = Color * sample;
}
