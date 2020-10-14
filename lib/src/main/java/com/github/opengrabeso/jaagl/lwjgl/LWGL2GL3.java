package com.github.opengrabeso.jaagl.lwjgl;

import com.github.opengrabeso.jaagl.GL2GL3;

import java.nio.FloatBuffer;

public abstract class LWGL2GL3 extends LWGL implements GL2GL3 {

    public LWGL2GL3() {
        super();
    }

    @Override
    public GL2GL3 getGL2GL3() {
        return this;
    }

    @Override
    public int GL_VERTEX_SHADER() {
        return org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
    }

    @Override
    public int GL_FRAGMENT_SHADER() {
        return org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
    }

    @Override
    public int GL_GEOMETRY_SHADER() {
        return org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
    }

    public void glUseProgram(int program) {
        org.lwjgl.opengl.GL20.glUseProgram(program);
    }

    public void glDeleteProgram(int program) {
        org.lwjgl.opengl.GL20.glDeleteProgram(program);
    }

    public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int i1) {
        assert (count * 16 == value.length);
        assert (i1 == 0);
        org.lwjgl.opengl.GL20.glUniformMatrix4fv(location, transpose, value);
    }

    public int glCreateProgram() {
        return org.lwjgl.opengl.GL20.glCreateProgram();
    }

    public void glAttachShader(int program, int vs) {
        org.lwjgl.opengl.GL20.glAttachShader(program, vs);
    }

    public void glLinkProgram(int program) {
        org.lwjgl.opengl.GL20.glLinkProgram(program);
    }

    public void glValidateProgram(int program) {
        org.lwjgl.opengl.GL20.glValidateProgram(program);
    }

    public void glDeleteShader(int vs) {
        org.lwjgl.opengl.GL20.glDeleteShader(vs);
    }

    public void glGenBuffers(int[] handles) {
        org.lwjgl.opengl.GL20.glGenBuffers(handles);
    }

    public int GL_ARRAY_BUFFER() {
        return org.lwjgl.opengl.GL20.GL_ARRAY_BUFFER;
    }

    public int GL_STREAM_DRAW() {
        return org.lwjgl.opengl.GL20.GL_STREAM_DRAW;
    }

    public void glBindBuffer(int gl_array_buffer, int vbo) {
        org.lwjgl.opengl.GL20.glBindBuffer(gl_array_buffer, vbo);
    }

    public void glBufferData(int gl_array_buffer, int size, FloatBuffer o, int gl_stream_draw) {
        if (o == null) {
            org.lwjgl.opengl.GL20.glBufferData(gl_array_buffer, size, gl_stream_draw);
        } else {
            o.rewind();
            assert o.isDirect();
            assert size <= o.remaining() * 4;
            final FloatBuffer buffer = o.slice();
            buffer.position(size / 4);
            buffer.flip();
            assert size == buffer.remaining() * 4;
            org.lwjgl.opengl.GL20.glBufferData(gl_array_buffer, buffer, gl_stream_draw);
        }
    }

    public int GL_VALIDATE_STATUS() {
        return org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
    }

    public int GL_LINK_STATUS() {
        return org.lwjgl.opengl.GL20.GL_LINK_STATUS;
    }

    public int GL_COMPILE_STATUS() {
        return org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
    }

    public int glCreateShader(int type) {
        return org.lwjgl.opengl.GL20.glCreateShader(type);
    }

    public void glShaderSource(int shader, String source) {
        org.lwjgl.opengl.GL20.glShaderSource(shader, source);
    }

    public void glCompileShader(int shader) {
        org.lwjgl.opengl.GL20.glCompileShader(shader);
    }

    public int GL_VERTEX_ARRAY() {
        return org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
    }

    public int GL_TEXTURE_COORD_ARRAY() {
        return org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
    }

    public void glDeleteBuffers(int[] handles) {
        org.lwjgl.opengl.GL20.glDeleteBuffers(handles);
    }

    public void glBufferSubData(int gl_array_buffer, int offsetInBytes, int sizeInBytes, FloatBuffer data) {
        assert data.isDirect();
        data.position(offsetInBytes / 4);
        assert sizeInBytes <= data.remaining() * 4;
        final FloatBuffer buffer = data.slice();
        buffer.position(sizeInBytes / 4);
        buffer.flip();
        assert sizeInBytes == buffer.remaining() * 4;
        assert buffer.isDirect();
        org.lwjgl.opengl.GL20.glBufferSubData(gl_array_buffer, 0, buffer);
    }

    public void glDrawArrays(int gl_quads, int i, int sizeInVertices) {
        org.lwjgl.opengl.GL20.glDrawArrays(gl_quads, i, sizeInVertices);
    }

    public void glEnableVertexAttribArray(int coordLoc) {
        org.lwjgl.opengl.GL20.glEnableVertexAttribArray(coordLoc);
    }

    public int glGetAttribLocation(int program, String coordAttribName) {
        return org.lwjgl.opengl.GL20.glGetAttribLocation(program, coordAttribName);
    }

    public void glBindVertexArray(int i) {
        org.lwjgl.opengl.GL30C.glBindVertexArray(i);
    }

    public void glVertexAttribPointer(int coordLoc, int floatsPerCoord, int gl_float, boolean b, int stride, int coordOffset) {
        org.lwjgl.opengl.GL20.glVertexAttribPointer(coordLoc, floatsPerCoord, gl_float, b, stride, coordOffset);
    }

    public void glGenVertexArrays(int[] handles) {
        org.lwjgl.opengl.GL30.glGenVertexArrays(handles);
    }

    public void glDisableVertexAttribArray(int vertCoordLocation) {
        org.lwjgl.opengl.GL20.glDisableVertexAttribArray(vertCoordLocation);
    }

    public void glDeleteVertexArrays(int[] handles) {
        org.lwjgl.opengl.GL30.glDeleteVertexArrays(handles);
    }

    @Override
    public int glGetUniformLocation(int program, String name) {
        return org.lwjgl.opengl.GL20.glGetUniformLocation(program, name);
    }

    @Override
    public void glUniform4fv(int location, int i, float[] value, int i1) {
        assert i * 4 == value.length;
        assert i1 == 0;
        org.lwjgl.opengl.GL20.glUniform4fv(location, value);
    }

    @Override
    public int GL_INFO_LOG_LENGTH() {
        return org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
    }

    @Override
    public void glGetShaderiv(int shaderObj, int name, int[] infoLogLength, int i) {
        assert i == 0;
        org.lwjgl.opengl.GL20.glGetShaderiv(shaderObj, name, infoLogLength);
    }

    @Override
    public String glGetShaderInfoLog(int shaderObj) {
        return org.lwjgl.opengl.GL20.glGetShaderInfoLog(shaderObj);
    }

    @Override
    public void glGetProgramiv(int programObj, int pname, int[] infoLogLength, int i) {
        assert i == 0;
        org.lwjgl.opengl.GL20.glGetProgramiv(programObj, pname, infoLogLength);
    }

    @Override
    public String glGetProgramInfoLog(int programObj) {
        return org.lwjgl.opengl.GL20.glGetProgramInfoLog(programObj);
    }

    @Override
    public boolean glIsProgram(int programObj) {
        return org.lwjgl.opengl.GL20.glIsProgram(programObj);
    }

    @Override
    public void glDetachShader(int program, int i) {
        org.lwjgl.opengl.GL20.glDetachShader(program, i);
    }

    @Override
    public boolean glIsBuffer(int bufferId) {
        return org.lwjgl.opengl.GL20.glIsBuffer(bufferId);
    }


    @Override
    public void glUniform1i(int textureLocation, int unit) {
        org.lwjgl.opengl.GL20.glUniform1i(textureLocation, unit);
    }

    @Override
    public void glUniform1f(int lineWidthLocation, float lineWidth) {
        org.lwjgl.opengl.GL20.glUniform1f(lineWidthLocation, lineWidth);
    }

    @Override
    public int GL_GEOMETRY_INPUT_TYPE() {
        return org.lwjgl.opengl.GL32.GL_GEOMETRY_INPUT_TYPE;
    }

    @Override
    public int GL_GEOMETRY_OUTPUT_TYPE() {
        return org.lwjgl.opengl.GL32.GL_GEOMETRY_OUTPUT_TYPE;
    }

    @Override
    public int GL_GEOMETRY_VERTICES_OUT() {
        return org.lwjgl.opengl.GL32.GL_GEOMETRY_OUTPUT_TYPE;
    }

    @Override
    public void glProgramParameteri(int programId, int gl_geometry_input_type, int gl_lines) {
        org.lwjgl.opengl.GL41.glProgramParameteri(programId, gl_geometry_input_type, gl_lines);
    }

    @Override
    public void glPointSize(float glLineWidth) {
        org.lwjgl.opengl.GL20.glPointSize(glLineWidth);
    }


}
