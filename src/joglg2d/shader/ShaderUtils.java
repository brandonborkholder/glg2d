package joglg2d.shader;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

public class ShaderUtils {
  private static IntBuffer buf = BufferUtil.newIntBuffer(1);

  private static ByteBuffer stringBuf = BufferUtil.newByteBuffer(1024);

  public static boolean checkProgramError(GL gl, int target, int type) {
    gl.glGetProgramiv(target, type, buf);
    return buf.get(0) == 0;
  }

  public static boolean checkShaderError(GL gl, int target, int type) {
    gl.glGetProgramiv(target, type, buf);
    gl.glGetShaderiv(target, type, buf);
    return buf.get(0) == 0;
  }

  public static String getShaderLog(GL gl, int target) {
    gl.glGetShaderInfoLog(target, stringBuf.capacity(), buf, stringBuf);
    if (buf.get(0) > 0) {
      byte[] bytes = new byte[buf.get(0)];
      stringBuf.get(bytes, 0, bytes.length);
      return new String(bytes);
    } else {
      return "";
    }
  }

  public static String getProgramLog(GL gl, int target) {
    gl.glGetProgramInfoLog(target, stringBuf.capacity(), buf, stringBuf);
    if (buf.get(0) > 0) {
      byte[] bytes = new byte[buf.get(0)];
      stringBuf.get(bytes, 0, bytes.length);
      return new String(bytes);
    } else {
      return "";
    }
  }
}
