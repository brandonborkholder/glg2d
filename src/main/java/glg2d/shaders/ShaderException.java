package glg2d.shaders;

public class ShaderException extends RuntimeException {
  private static final long serialVersionUID = 829519650852350876L;

  public ShaderException() {
    super();
  }

  public ShaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ShaderException(String message, Throwable cause) {
    super(message, cause);
  }

  public ShaderException(String message) {
    super(message);
  }

  public ShaderException(Throwable cause) {
    super(cause);
  }
}
