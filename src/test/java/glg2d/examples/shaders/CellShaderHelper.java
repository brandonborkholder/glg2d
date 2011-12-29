package glg2d.examples.shaders;

import javax.media.opengl.GL;
import javax.media.opengl.GLContext;

public class CellShaderHelper {
  private GLContext context;

  private int shaderprogram;

  public CellShaderHelper(GLContext gl) {
    context = gl;
  }

  public void setupShader() {
    GL gl = context.getGL();
    int v = gl.glCreateShader(GL.GL_VERTEX_SHADER);
    int f = gl.glCreateShader(GL.GL_FRAGMENT_SHADER);

    String src = "void main(void) { vec4 color; gl_Position = ftransform();" +
//        "color = vec4(0,0,0,0);" +
//        "color += gl_FrontLightModelProduct.sceneColor;" +
//        "color = gl_FrontMaterial.ambient;" +
//        "color += gl_FrontMaterial.diffuse;" +
//        "color += gl_FrontMaterial.specular;" +
        "gl_TexCoord[0] = gl_TextureMatrix[0] * gl_MultiTexCoord0;" +
        "color = clamp( color, 0.0, 1.0 );" +
//        "color = vec4(1.0, 0.0, 0.0, 1.0);" +
        "gl_FrontColor = gl_Color;" +
        "}";
    gl.glShaderSource(v, 1, new String[] { src }, new int[] { src.length() }, 0);
    gl.glCompileShader(v);

//    src = "void main(void) { gl_FragColor = gl_Color; }";
    src = "uniform sampler2D tex;" +
	"void main(void) {" +
        "vec4 texel;" +
        "vec4 color = gl_Color;" +
        "vec2 si = textureSize2D(tex, 0);" +
//        "vec4 color = vec4(1,0,0,1);" +
	"texel = texture2D(tex, gl_TexCoord[0].st);" +
        "if (si[0] > 0) {" +
//	"color = vec4(color.rgb * texel.rgb, color.a * texel.a);" +
        // good when there is a texture, bad if not
	"color = color * texel;" +
        "}" +
//        "color.r = round(color.r * 3) / 3;" +
//        "color.g = round(color.g * 3) / 3;" +
//        "color.b = round(color.b * 3) / 3;" +
	"gl_FragColor = color;" +
	"}";
    gl.glShaderSource(f, 1, new String[] { src }, new int[] { src.length() }, 0);
    gl.glCompileShader(v);
    gl.glCompileShader(f);

    shaderprogram = gl.glCreateProgram();
    gl.glAttachShader(shaderprogram, v);
    gl.glAttachShader(shaderprogram, f);
    gl.glLinkProgram(shaderprogram);
    gl.glValidateProgram(shaderprogram);

    gl.glUseProgram(shaderprogram);
  }

  public void endShader() {
    GL gl = context.getGL();
    gl.glDeleteProgram(shaderprogram);
  }
}
