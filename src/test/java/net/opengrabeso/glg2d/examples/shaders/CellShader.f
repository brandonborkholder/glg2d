void main() {
  int prec = 4;
  gl_FragColor = vec4(round(gl_Color.r * prec) / prec, round(gl_Color.g * prec) / prec, round(gl_Color.b * prec) / prec, gl_Color.a);
}