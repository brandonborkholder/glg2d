uniform sampler2D tex;

void main() {
  vec4 texel;
  vec4 color = gl_Color;

  texel = texture2D(tex, gl_TexCoord[0].st);
  gl_FragColor = vec4(color.rgb * texel.rgb, texel.a);
}