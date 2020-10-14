package com.github.opengrabeso.jaagl.lwjgl;

import com.github.opengrabeso.jaagl.GL2;
import org.lwjgl.opengl.GL11;

import java.nio.Buffer;
import java.util.Arrays;
import java.util.HashSet;

import static org.lwjgl.system.MemoryUtil.memAddress;

public class LWGL2 extends LWGL2GL3 implements GL2 {

    HashSet<String> extensions;

    public LWGL2() {
        super();

        extensions = new HashSet<String>();

        String extensionString = GL11.glGetString(GL11.GL_EXTENSIONS);
        if (extensionString != null) {
            String[] ext = extensionString.split(" ");
            extensions.addAll(Arrays.asList(ext));
        }


    }

    @Override
    public GL2 getGL2() {
        return this;
    }


    @Override
    public boolean isExtensionAvailable(String name) {
        return extensions.contains(name);
    }


    @Override
    public int GL_TEXTURE_ENV() {
        return GL11.GL_TEXTURE_ENV;
    }

    @Override
    public int GL_TEXTURE_ENV_MODE() {
        return GL11.GL_TEXTURE_ENV_MODE;
    }

    @Override
    public int GL_MODULATE() {
        return GL11.GL_MODULATE;
    }

    public long GL_ALL_CLIENT_ATTRIB_BITS() {
        return GL11.GL_CLIENT_ALL_ATTRIB_BITS;
    }

    @Override
    public int GL_SCISSOR_BIT() {
        return org.lwjgl.opengl.GL20.GL_SCISSOR_BIT;
    }

    public void glPushClientAttrib(int gl_all_client_attrib_bits) {
        GL11.glPushClientAttrib(gl_all_client_attrib_bits);
    }

    public void glEnableClientState(int gl_vertex_array) {
        GL11.glEnableClientState(gl_vertex_array);
    }

    public void glVertexPointer(int floatsPerPoint, int gl_float, int stride, int pointOffset) {
        GL11.glVertexPointer(floatsPerPoint, gl_float, stride, pointOffset);
    }

    public void glVertexPointer(int floatsPerPoint, int gl_float, int stride, Buffer buffer) {
        assert buffer.isDirect();
        GL11.glVertexPointer(floatsPerPoint, gl_float, stride, memAddress(buffer));
    }

    public void glTexCoordPointer(int floatsPerCoord, int gl_float, int stride, int coordOffset) {
        GL11.glTexCoordPointer(floatsPerCoord, gl_float, stride, coordOffset);
    }

    public int GL_QUADS() {
        return org.lwjgl.opengl.GL20.GL_QUADS;
    }

    public void glPopClientAttrib() {
        GL11.glPopClientAttrib();
    }

    public int GL_LIGHTING() {
        return org.lwjgl.opengl.GL20.GL_LIGHTING;
    }

    public void glPushAttrib(int attribMask) {
        GL11.glPushAttrib(attribMask);
    }

    public int GL_PROJECTION() {
        return org.lwjgl.opengl.GL20.GL_PROJECTION;
    }

    public int GL_MODELVIEW() {
        return org.lwjgl.opengl.GL20.GL_MODELVIEW;
    }

    public void glTexEnvi(int gl_texture_env, int gl_texture_env_mode, int gl_modulate) {
        GL11.glTexEnvi(gl_texture_env, gl_texture_env_mode, gl_modulate);
    }

    @Override
    public void glMatrixMode(int gl_projection) {
        GL11.glMatrixMode(gl_projection);
    }

    public void glPushMatrix() {
        GL11.glPushMatrix();
    }

    public void glLoadIdentity() {
        GL11.glLoadIdentity();
    }

    public void glOrtho(int i, int width, int i1, int height, int i2, int i3) {
        GL11.glOrtho(i, width, i1, height, i2, i3);
    }

    public void glScalef(float x, float y, float z) {
        GL11.glScalef(x, y, z);
    }

    public void glTranslatef(float x, float y, float z){
        GL11.glTranslatef(x, y, z);
    }

    public void glPopMatrix() {
        GL11.glPopMatrix();
    }

    public void glPopAttrib() {
        GL11.glPopAttrib();
    }

    @Override
    public void glColor4f(float r, float g, float b, float a) {
        GL11.glColor4f(r, g, b, a);
    }

    public int GL_ENABLE_BIT() {
        return org.lwjgl.opengl.GL20.GL_ENABLE_BIT;
    }

    public int GL_TEXTURE_BIT() {
        return org.lwjgl.opengl.GL20.GL_TEXTURE_BIT;
    }

    public int GL_TRANSFORM_BIT() {
        return org.lwjgl.opengl.GL20.GL_TRANSFORM_BIT;
    }

    public int GL_INTENSITY() {
        return org.lwjgl.opengl.GL20.GL_INTENSITY;
    }

    public int GL_LUMINANCE() {
        return org.lwjgl.opengl.GL20.GL_LUMINANCE;
    }

    @Override
    public void glBegin(int gl_quads) {
        GL11.glBegin(gl_quads);
    }

    @Override
    public void glTexCoord2f(float sx1, float sy2) {
        GL11.glTexCoord2f(sx1, sy2);
    }

    @Override
    public void glVertex2i(int dx1, int dy2) {
        GL11.glVertex2i(dx1, dy2);
    }

    @Override
    public void glVertex3f(float x, float y, float z) {
        GL11.glVertex3f(x, y, z);
    }

    @Override
    public void glEnd() {
        GL11.glEnd();
    }

    @Override
    public int GL_POLYGON() {
        return org.lwjgl.opengl.GL20.GL_POLYGON;
    }

    @Override
    public void glDisableClientState(int gl_vertex_array) {
        GL11.glDisableClientState(gl_vertex_array);
    }

    @Override
    public int GL_LINE_STIPPLE() {
        return org.lwjgl.opengl.GL20.GL_LINE_STIPPLE;
    }

    @Override
    public void glLineStipple(int i, short mask) {
        GL11.glLineStipple(i, mask);
    }

    @Override
    public int GL_MODELVIEW_MATRIX() {
        return org.lwjgl.opengl.GL20.GL_MODELVIEW_MATRIX;
    }

    @Override
    public int GL_LINE_BIT() {
        return org.lwjgl.opengl.GL20.GL_LINE_BIT;
    }

    @Override
    public int GL_POINT_BIT() {
        return org.lwjgl.opengl.GL20.GL_POINT_BIT;
    }

    @Override
    public void glColor4ub(byte b, byte b1, byte b2, byte b3) {
        GL11.glColor4ub(b, b1, b2, b3);
    }

    @Override
    public void glRasterPos2i(int x2, int y2) {
        GL11.glRasterPos2i(x2, y2);
    }

    @Override
    public int GL_COLOR() {
        return org.lwjgl.opengl.GL20.GL_COLOR;
    }

    @Override
    public void glCopyPixels(int x1, int y1, int width, int height, int gl_color) {
        GL11.glCopyPixels(x1, y1, width, height, gl_color);
    }

    @Override
    public void glLoadMatrixf(float[] matrix, int i) {
        assert(i == 0);
        GL11.glLoadMatrixf(matrix);
    }
}
