package com.github.opengrabeso.jaagl.lwjgl;

import com.github.opengrabeso.jaagl.GL;
import com.github.opengrabeso.jaagl.GL2;
import com.github.opengrabeso.jaagl.GL3;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public abstract class LWGL implements GL {

    LWGL() {
    }

    public static GL2 createGL2() {
        return new LWGL2();
    }

    public static GL3 createGL3() {
        return new LWGL3();
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
        org.lwjgl.opengl.GL11.glViewport(i, i1, i2, i3);
    }

    @Override
    public int glGetError() {
        return org.lwjgl.opengl.GL11.glGetError();
    }

    @Override
    public int GL_NO_ERROR() {
        return org.lwjgl.opengl.GL11.GL_NO_ERROR;
    }

    @Override
    public int GL_TRUE() {
        return org.lwjgl.opengl.GL11.GL_TRUE;
    }

    @Override
    public int GL_TEXTURE0() {
        return org.lwjgl.opengl.GL13.GL_TEXTURE0;
    }

    @Override
    public int GL_SCISSOR_TEST() {
        return org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
    }

    @Override
    public int GL_SRC_ALPHA() {
        return org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
    }

    @Override
    public int GL_ONE_MINUS_SRC_ALPHA() {
        return org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
    }

    @Override
    public int GL_TEXTURE_2D() {
        return org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
    }

    @Override
    public int GL_TEXTURE() {
        return org.lwjgl.opengl.GL11.GL_TEXTURE;
    }

    @Override
    public int GL_VIEWPORT() {
        return org.lwjgl.opengl.GL11.GL_VIEWPORT;
    }

    @Override
    public void glDisable(int par) {
        org.lwjgl.opengl.GL11.glDisable(par);
    }

    @Override
    public void glEnable(int par) {
        org.lwjgl.opengl.GL11.glEnable(par);
    }

    @Override
    public int GL_BLEND() {
        return org.lwjgl.opengl.GL11.GL_BLEND;
    }

    @Override
    public int GL_DEPTH_TEST() {
        return org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
    }

    @Override
    public int GL_CULL_FACE() {
        return org.lwjgl.opengl.GL11.GL_CULL_FACE;
    }

    @Override
    public void glActiveTexture(int unit) {
        org.lwjgl.opengl.GL13.glActiveTexture(unit);
    }

    @Override
    public void glBindTexture(int type, int handle) {
        org.lwjgl.opengl.GL13.glBindTexture(type, handle);
    }

    @Override
    public void glDeleteTextures(int[] handles) {
        org.lwjgl.opengl.GL11.glDeleteTextures(handles);
    }

    @Override
    public void glGenTextures(int[] handles) {
        org.lwjgl.opengl.GL11.glGenTextures(handles);
    }

    @Override
    public int GL_LINEAR() {
        return org.lwjgl.opengl.GL11.GL_LINEAR;
    }

    @Override
    public int GL_LINEAR_MIPMAP_NEAREST() {
        return org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_NEAREST;
    }

    @Override
    public int GL_NEAREST() {
        return org.lwjgl.opengl.GL11.GL_NEAREST;
    }

    @Override
    public int GL_NEAREST_MIPMAP_NEAREST() {
        return org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
    }

    @Override
    public int GL_TEXTURE_MAG_FILTER() {
        return org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
    }

    @Override
    public int GL_TEXTURE_MIN_FILTER() {
        return org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
    }

    @Override
    public void glTexParameteri(int type, int name, int value) {
        org.lwjgl.opengl.GL11.glTexParameteri(type, name, value);
    }

    @Override
    public int GL_MAX_TEXTURE_SIZE() {
        return org.lwjgl.opengl.GL11.GL_MAX_TEXTURE_SIZE;
    }

    @Override
    public void glGetIntegerv(int name, int[] size) {
        org.lwjgl.opengl.GL11.glGetIntegerv(name, size);
    }

    @Override
    public int glGetInteger(int name) {
        int[] ret = new int[]{0};
        org.lwjgl.opengl.GL11.glGetIntegerv(name, ret);
        return ret[0];
    }

    @Override
    public int GL_UNPACK_ALIGNMENT() {
        return org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
    }

    @Override
    public int GL_UNPACK_SKIP_ROWS() {
        return org.lwjgl.opengl.GL11.GL_UNPACK_SKIP_ROWS;
    }

    @Override
    public int GL_UNPACK_SKIP_PIXELS() {
        return org.lwjgl.opengl.GL11.GL_UNPACK_SKIP_PIXELS;
    }

    @Override
    public int GL_UNPACK_ROW_LENGTH() {
        return org.lwjgl.opengl.GL11.GL_UNPACK_ROW_LENGTH;
    }

    @Override
    public void glPixelStorei(int gl_unpack_alignment, int i) {
        org.lwjgl.opengl.GL11.glPixelStorei(gl_unpack_alignment, i);
    }

    @Override
    public int GL_UNSIGNED_BYTE() {
        return org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
    }

    @Override
    public int GL_TEXTURE_1D() {
        return org.lwjgl.opengl.GL11.GL_TEXTURE_1D;
    }

    @Override
    public int GL_TEXTURE_3D() {
        return org.lwjgl.opengl.GL12.GL_TEXTURE_3D;
    }

    @Override
    public int GL_TEXTURE31() {
        return org.lwjgl.opengl.GL13.GL_TEXTURE31;
    }

    @Override
    public int GL_COLOR_BUFFER_BIT() {
        return org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
    }

    @Override
    public int GL_DEPTH_BUFFER_BIT() {
        return org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
    }

    @Override
    public void glTexImage2D(int gl_texture_2D, int i, int i1, int size, int size1, int i2, int i3, int gl_unsigned_byte, ByteBuffer buffer) {
        assert buffer == null || buffer.isDirect();
        org.lwjgl.opengl.GL11.glTexImage2D(gl_texture_2D, i, i1, size, size1, i2, i3, gl_unsigned_byte, buffer);
    }

    @Override
    public void glTexSubImage2D(int gl_texture_2D, int i, int x, int y, int width, int height, int format, int gl_unsigned_byte, ByteBuffer buffer) {

        if (buffer.isDirect()) {
            org.lwjgl.opengl.GL11.glTexSubImage2D(gl_texture_2D, i, x, y, width, height, format, gl_unsigned_byte, buffer);
        } else {
            ByteBuffer direct = MemoryUtil.memAlloc(buffer.capacity());
            direct.put(buffer.duplicate());
            direct.flip();

            org.lwjgl.opengl.GL11.glTexSubImage2D(gl_texture_2D, i, x, y, width, height, format, gl_unsigned_byte, direct);

            MemoryUtil.memFree(direct);
        }
    }

    @Override
    public void glGenerateMipmap(int gl_texture_2D) {
        org.lwjgl.opengl.GL30.glGenerateMipmap(gl_texture_2D);
    }

    @Override
    public boolean glIsEnabled(int gl_depth_test) {
        return org.lwjgl.opengl.GL11.glIsEnabled(gl_depth_test);
    }

    @Override
    public int GL_ONE() {
        return org.lwjgl.opengl.GL11.GL_ONE;
    }

    @Override
    public void glBlendFunc(int gl_one, int gl_one_minus_src_alpha) {
        org.lwjgl.opengl.GL11.glBlendFunc(gl_one, gl_one_minus_src_alpha);
    }

    @Override
    public RuntimeException newGLException(String log) {
        return new RuntimeException("GL Exception " + log);
    }

    @Override
    public int GL_FLOAT() {
        return org.lwjgl.opengl.GL11.GL_FLOAT;
    }

    @Override
    public int GL_RGB() {
        return org.lwjgl.opengl.GL11.GL_RGB;
    }

    @Override
    public void glFlush() {
        org.lwjgl.opengl.GL11.glFlush();
    }

    @Override
    public int GL_TRIANGLES() {
        return org.lwjgl.opengl.GL11.GL_TRIANGLES;
    }

    @Override
    public void glClearColor(float i, float i1, float i2, float i3) {
        org.lwjgl.opengl.GL11.glClearColor(i, i1, i2, i3);
    }

    @Override
    public void glClear(int gl_color_buffer_bit) {
        org.lwjgl.opengl.GL11.glClear(gl_color_buffer_bit);
    }

    @Override
    public int GL_LINEAR_MIPMAP_LINEAR() {
        return org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
    }

    @Override
    public int GL_CLAMP_TO_EDGE() {
        return org.lwjgl.opengl.GL13.GL_CLAMP_TO_EDGE;
    }

    @Override
    public int GL_TEXTURE_WRAP_S() {
        return org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
    }

    @Override
    public int GL_TEXTURE_WRAP_T() {
        return org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
    }

    @Override
    public int GL_TEXTURE_CUBE_MAP() {
        return org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
    }

    @Override
    public int GL_TEXTURE_WRAP_R() {
        return org.lwjgl.opengl.GL13.GL_TEXTURE_WRAP_R;
    }

    @Override
    public int GL_GENERATE_MIPMAP() {
        return org.lwjgl.opengl.GL14.GL_GENERATE_MIPMAP;
    }

    @Override
    public void glTexParameterf(int gl_texture_env, int gl_texture_env_mode, float gl_blend) {
        org.lwjgl.opengl.GL11.glTexParameterf(gl_texture_env, gl_texture_env_mode, gl_blend);
    }

    @Override
    public int GL_STATIC_DRAW() {
        return org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
    }

    @Override
    public int GL_TRIANGLE_STRIP() {
        return org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
    }

    @Override
    public int GL_TRIANGLE_FAN() {
        return org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
    }

    @Override
    public int GL_LINES() {
        return org.lwjgl.opengl.GL11.GL_LINES;
    }

    @Override
    public void glLineWidth(float glLineWidth) {
        org.lwjgl.opengl.GL11.glLineWidth(glLineWidth);
    }

    @Override
    public void glGetFloatv(Object gl_modelview_matrix, float[] testMatrix, int i) {

    }

    @Override
    public int GL_LINE_LOOP() {
        return org.lwjgl.opengl.GL11.GL_LINE_LOOP;
    }

    @Override
    public int GL_LINE_STRIP() {
        return org.lwjgl.opengl.GL11.GL_LINE_STRIP;
    }

    @Override
    public int GL_POINTS() {
        return org.lwjgl.opengl.GL11.GL_POINTS;
    }

    @Override
    public int GL_MULTISAMPLE() {
        return org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
    }

    @Override
    public int GL_RGBA() {
        return org.lwjgl.opengl.GL11.GL_RGBA;
    }

    @Override
    public void glScissor(int x, int i, int max, int max1) {
        org.lwjgl.opengl.GL11.glScissor(x, i, max, max1);
    }

    @Override
    public int GL_ZERO() {
        return org.lwjgl.opengl.GL11.GL_ZERO;
    }
}
