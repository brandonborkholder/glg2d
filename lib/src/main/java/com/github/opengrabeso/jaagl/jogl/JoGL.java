package com.github.opengrabeso.jaagl.jogl;

import com.github.opengrabeso.jaagl.*;
import com.jogamp.opengl.GLException;

import java.nio.ByteBuffer;

public abstract class JoGL extends GLBase {
    com.jogamp.opengl.GL gl;

    JoGL(com.jogamp.opengl.GL gl) {
        super(gl.glGetString(gl.GL_VERSION));
        this.gl = gl;
    }

    public static GL2GL3 wrap(com.jogamp.opengl.GL gl) {
        // JOGL always uses GL4bcImpl to implement the interface
        // we need to check the profile to learn the real capabilities
        if (gl.getGLProfile().isGL3()) {
            return new JoGL3((com.jogamp.opengl.GL3) gl);
        } else if (gl instanceof com.jogamp.opengl.GL2) {
            return new JoGL2((com.jogamp.opengl.GL2) gl);
        } else {
            throw new UnsupportedOperationException("GL2 or GL3 expected");
        }
    }

    public static GL2 wrap(com.jogamp.opengl.GL2 gl) {
        return new JoGL2(gl);
    }

    public static GL3 wrap(com.jogamp.opengl.GL3 gl) {
        return new JoGL3(gl);
    }

    public GL2ES2 jogl() {
        return (GL2ES2) gl;
    }

    @Override
    public GL2 getGL2() {
        return null;
    }

    @Override
    public GL3 getGL3() {
        return null;
    }

    @Override
    public GL3 gl3() {
        return getGL3();
    }

    @Override
    public boolean isGL3() {
        return getGL3() != null;
    }

    @Override
    public GL2 gl2() {
        return getGL2();
    }

    @Override
    public void glViewport(int i, int i1, int i2, int i3) {
        gl.glViewport(i, i1, i2, i3);
    }

    @Override
    public int glGetError() {
        return gl.glGetError();
    }

    @Override
    public int GL_NO_ERROR() {
        return com.jogamp.opengl.GL.GL_NO_ERROR;
    }

    @Override
    public int GL_TRUE() {
        return com.jogamp.opengl.GL.GL_TRUE;
    }

    @Override
    public boolean isExtensionAvailable(String name) {
        return gl.isExtensionAvailable(name);
    }

    @Override
    public int GL_TEXTURE0() {
        return com.jogamp.opengl.GL.GL_TEXTURE0;
    }

    @Override
    public int GL_SCISSOR_TEST() {
        return com.jogamp.opengl.GL.GL_SCISSOR_TEST;
    }

    @Override
    public int GL_SRC_ALPHA() {
        return com.jogamp.opengl.GL.GL_SRC_ALPHA;
    }

    @Override
    public int GL_ONE_MINUS_SRC_ALPHA() {
        return com.jogamp.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
    }

    @Override
    public int GL_TEXTURE_2D() {
        return com.jogamp.opengl.GL.GL_TEXTURE_2D;
    }

    @Override
    public int GL_TEXTURE() {
        return com.jogamp.opengl.GL.GL_TEXTURE;
    }

    @Override
    public int GL_VIEWPORT() {
        return com.jogamp.opengl.GL.GL_VIEWPORT;
    }

    @Override
    public void glDisable(int par) {
        gl.glDisable(par);
    }

    @Override
    public void glEnable(int par) {
        gl.glEnable(par);
    }

    @Override
    public int GL_BLEND() {
        return com.jogamp.opengl.GL.GL_BLEND;
    }

    @Override
    public int GL_DEPTH_TEST() {
        return com.jogamp.opengl.GL.GL_DEPTH_TEST;
    }

    @Override
    public int GL_CULL_FACE() {
        return com.jogamp.opengl.GL.GL_CULL_FACE;
    }

    @Override
    public void glActiveTexture(int unit) {
        gl.glActiveTexture(unit);
    }

    @Override
    public void glBindTexture(int type, int handle) {
        gl.glBindTexture(type, handle);
    }

    @Override
    public void glDeleteTextures(int[] handles) {
        gl.glDeleteTextures(handles.length, handles, 0);
    }

    @Override
    public void glGenTextures(int[] handles) {
        gl.glGenTextures(handles.length, handles, 0);
    }

    @Override
    public int GL_LINEAR() {
        return com.jogamp.opengl.GL.GL_LINEAR;
    }

    @Override
    public int GL_LINEAR_MIPMAP_NEAREST() {
        return com.jogamp.opengl.GL.GL_LINEAR_MIPMAP_NEAREST;
    }

    @Override
    public int GL_NEAREST() {
        return com.jogamp.opengl.GL.GL_NEAREST;
    }

    @Override
    public int GL_NEAREST_MIPMAP_NEAREST() {
        return com.jogamp.opengl.GL.GL_NEAREST_MIPMAP_NEAREST;
    }

    @Override
    public int GL_TEXTURE_MAG_FILTER() {
        return com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
    }

    @Override
    public int GL_TEXTURE_MIN_FILTER() {
        return com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
    }

    @Override
    public void glTexParameteri(int type, int name, int value) {
        gl.glTexParameteri(type, name, value);
    }

    @Override
    public int GL_MAX_TEXTURE_SIZE() {
        return com.jogamp.opengl.GL.GL_MAX_TEXTURE_SIZE;
    }

    @Override
    public void glGetIntegerv(int name, int[] size) {
        gl.glGetIntegerv(name, size, 0);
    }

    @Override
    public int glGetInteger(int name) {
        int[] ret = new int[]{0};
        gl.glGetIntegerv(name, ret, 0);
        return ret[0];
    }


    @Override
    public int GL_UNPACK_ALIGNMENT() {
        return com.jogamp.opengl.GL.GL_UNPACK_ALIGNMENT;
    }

    @Override
    public int GL_UNPACK_SKIP_ROWS() {
        return com.jogamp.opengl.GL2ES2.GL_UNPACK_SKIP_ROWS;
    }

    @Override
    public int GL_UNPACK_SKIP_PIXELS() {
        return com.jogamp.opengl.GL2ES2.GL_UNPACK_SKIP_PIXELS;
    }

    @Override
    public int GL_UNPACK_ROW_LENGTH() {
        return com.jogamp.opengl.GL2ES2.GL_UNPACK_ROW_LENGTH;
    }

    @Override
    public void glPixelStorei(int gl_unpack_alignment, int i) {
        gl.glPixelStorei(gl_unpack_alignment, i);
    }

    @Override
    public int GL_UNSIGNED_BYTE() {
        return com.jogamp.opengl.GL.GL_UNSIGNED_BYTE;
    }

    @Override
    public int GL_TEXTURE_1D() {
        return com.jogamp.opengl.GL2GL3.GL_TEXTURE_1D;
    }

    @Override
    public int GL_TEXTURE_3D() {
        return com.jogamp.opengl.GL2GL3.GL_TEXTURE_3D;
    }

    @Override
    public int GL_TEXTURE31() {
        return com.jogamp.opengl.GL.GL_TEXTURE31;
    }

    @Override
    public int GL_COLOR_BUFFER_BIT() {
        return com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
    }

    @Override
    public int GL_DEPTH_BUFFER_BIT() {
        return com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
    }

    @Override
    public void glTexImage2D(int gl_texture_2D, int i, int i1, int size, int size1, int i2, int i3, int gl_unsigned_byte, ByteBuffer buffer) {
        gl.glTexImage2D(gl_texture_2D, i, i1, size, size1, i2, i3, gl_unsigned_byte, buffer);
    }

    @Override
    public void glTexSubImage2D(int gl_texture_2D, int i, int x, int y, int width, int height, int format, int gl_unsigned_byte, ByteBuffer wrap) {
        gl.glTexSubImage2D(gl_texture_2D, i, x, y, width, height, format, gl_unsigned_byte, wrap);
    }

    @Override
    public void glGenerateMipmap(int gl_texture_2D) {
        gl.glGenerateMipmap(gl_texture_2D);
    }

    @Override
    public boolean glIsEnabled(int gl_depth_test) {
        return gl.glIsEnabled(gl_depth_test);
    }

    @Override
    public int GL_ONE() {
        return com.jogamp.opengl.GL.GL_ONE;
    }

    @Override
    public void glBlendFunc(int gl_one, int gl_one_minus_src_alpha) {
        gl.glBlendFunc(gl_one, gl_one_minus_src_alpha);
    }

    @Override
    public RuntimeException newGLException(String log) {
        return new GLException(log);
    }

    @Override
    public int GL_FLOAT() {
        return com.jogamp.opengl.GL.GL_FLOAT;
    }

    @Override
    public int GL_RGB() {
        return com.jogamp.opengl.GL.GL_RGB;
    }

    @Override
    public void glFlush() {
        gl.glFlush();
    }

    @Override
    public int GL_TRIANGLES() {
        return com.jogamp.opengl.GL.GL_TRIANGLES;
    }

    @Override
    public void glClearColor(float i, float i1, float i2, float i3) {
        gl.glClearColor(i, i1, i2, i3);
    }

    @Override
    public void glClear(int gl_color_buffer_bit) {
        gl.glClear(gl_color_buffer_bit);
    }

    @Override
    public int GL_LINEAR_MIPMAP_LINEAR() {
        return com.jogamp.opengl.GL.GL_LINEAR_MIPMAP_LINEAR;
    }

    @Override
    public int GL_CLAMP_TO_EDGE() {
        return com.jogamp.opengl.GL.GL_CLAMP_TO_EDGE;
    }

    @Override
    public int GL_TEXTURE_WRAP_S() {
        return com.jogamp.opengl.GL.GL_TEXTURE_WRAP_S;
    }

    @Override
    public int GL_TEXTURE_WRAP_T() {
        return com.jogamp.opengl.GL.GL_TEXTURE_WRAP_T;
    }

    @Override
    public int GL_TEXTURE_CUBE_MAP() {
        return com.jogamp.opengl.GL.GL_TEXTURE_CUBE_MAP;
    }

    @Override
    public int GL_TEXTURE_WRAP_R() {
        return com.jogamp.opengl.GL2ES2.GL_TEXTURE_WRAP_R;
    }

    @Override
    public int GL_GENERATE_MIPMAP() {
        return com.jogamp.opengl.GL2ES1.GL_GENERATE_MIPMAP;
    }

    @Override
    public void glTexParameterf(int gl_texture_env, int gl_texture_env_mode, float gl_blend) {
        gl.glTexParameterf(gl_texture_env, gl_texture_env_mode, gl_blend);
    }

    @Override
    public int GL_STATIC_DRAW() {
        return com.jogamp.opengl.GL.GL_STATIC_DRAW;
    }

    @Override
    public int GL_TRIANGLE_STRIP() {
        return com.jogamp.opengl.GL.GL_TRIANGLE_STRIP;
    }

    @Override
    public int GL_TRIANGLE_FAN() {
        return com.jogamp.opengl.GL.GL_TRIANGLE_FAN;
    }

    @Override
    public int GL_LINES() {
        return com.jogamp.opengl.GL.GL_LINES;
    }

    @Override
    public void glLineWidth(float glLineWidth) {
        gl.glLineWidth(glLineWidth);
    }

    @Override
    public void glGetFloatv(Object gl_modelview_matrix, float[] testMatrix, int i) {

    }

    @Override
    public int GL_LINE_LOOP() {
        return com.jogamp.opengl.GL.GL_LINE_LOOP;
    }

    @Override
    public int GL_LINE_STRIP() {
        return com.jogamp.opengl.GL.GL_LINE_STRIP;
    }

    @Override
    public int GL_POINTS() {
        return com.jogamp.opengl.GL.GL_POINTS;
    }

    @Override
    public int GL_MULTISAMPLE() {
        return com.jogamp.opengl.GL.GL_MULTISAMPLE;
    }

    @Override
    public int GL_RGBA() {
        return com.jogamp.opengl.GL.GL_RGBA;
    }

    @Override
    public void glScissor(int x, int i, int max, int max1) {
        gl.glScissor(x, i, max, max1);
    }

    @Override
    public int GL_ZERO() {
        return com.jogamp.opengl.GL.GL_ZERO;
    }
}
