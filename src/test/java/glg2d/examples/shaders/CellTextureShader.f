uniform sampler2D tex;

void main() {
  vec4 texel;
  vec4 color = gl_Color;
  int prec = 4;

  texel = texture2D(tex, gl_TexCoord[0].st);
  color = vec4(color.rgb * texel.rgb, texel.a);
  gl_FragColor = vec4(round(color.r * prec) / prec, round(color.g * prec) / prec, round(color.b * prec) / prec, color.a);
}