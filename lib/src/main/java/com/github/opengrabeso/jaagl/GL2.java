package com.github.opengrabeso.jaagl;

import java.nio.Buffer;

public interface GL2 extends GL, GL2GL3 {
    int GL_LIGHTING();

    void glPushAttrib(int attribMask);

    int GL_PROJECTION();

    int GL_MODELVIEW();

    void glBlendFunc(int gl_src_alpha, int gl_one_minus_src_alpha);

    void glTexEnvi(int gl_texture_env, int gl_texture_env_mode, int gl_modulate);

    void glMatrixMode(int gl_projection);

    void glPushMatrix();

    void glLoadIdentity();

    void glOrtho(int i, int width, int i1, int height, int i2, int i3);

    void glScalef(float x, float y, float z);

    void glTranslatef(float x, float y, float z);

    void glPopMatrix();

    void glPopAttrib();

    void glColor4f(float r, float g, float b, float a);

    int GL_ENABLE_BIT();

    int GL_TEXTURE_BIT();

    int GL_TRANSFORM_BIT();

    int GL_INTENSITY();

    int GL_LUMINANCE();

    int GL_TEXTURE_ENV();

    int GL_TEXTURE_ENV_MODE();

    int GL_MODULATE();

    long GL_ALL_CLIENT_ATTRIB_BITS();

    int GL_SCISSOR_BIT();

    void glPushClientAttrib(int gl_all_client_attrib_bits);

    void glEnableClientState(int gl_vertex_array);

    void glVertexPointer(int floatsPerPoint, int gl_float, int stride, int pointOffset);

    void glVertexPointer(int floatsPerPoint, int gl_float, int stride, Buffer buffer);

    void glTexCoordPointer(int floatsPerCoord, int gl_float, int stride, int coordOffset);

    int GL_QUADS();

    void glPopClientAttrib();

    void glBegin(int gl_quads);

    void glTexCoord2f(float sx1, float sy2);
    
    void glVertex3f(float x, float y, float z);

    void glVertex2i(int dx1, int dy2);

    void glEnd();

    int GL_POLYGON();

    void glDisableClientState(int gl_vertex_array);

    int GL_LINE_STIPPLE();

    void glLineStipple(int i, short mask);

    int GL_MODELVIEW_MATRIX();

    int GL_LINE_BIT();

    int GL_POINT_BIT();

    void glColor4ub(byte b, byte b1, byte b2, byte b3);

    void glRasterPos2i(int x2, int y2);

    int GL_COLOR();

    void glCopyPixels(int x1, int y1, int width, int height, int gl_color);

    void glLoadMatrixf(float[] matrix, int i);
}
