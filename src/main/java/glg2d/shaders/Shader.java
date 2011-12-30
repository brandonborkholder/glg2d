package glg2d.shaders;

public interface Shader {
  void compileVertexShader();

  void compileFragmentShader();

  void createAndAttach();

  void use(boolean use);

  void delete();
}