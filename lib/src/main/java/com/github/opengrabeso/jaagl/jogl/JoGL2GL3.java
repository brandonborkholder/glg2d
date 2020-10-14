package com.github.opengrabeso.jaagl.jogl;
import com.github.opengrabeso.jaagl.*;

import java.nio.FloatBuffer;

public class JoGL2GL3 extends JoGL implements GL2GL3 {
    private com.jogamp.opengl.GL2GL3 ggl() {
        return (com.jogamp.opengl.GL2GL3)this.gl;
    }

    public JoGL2GL3(com.jogamp.opengl.GL gl) {
        super(gl);
    }

    @Override
    public GL2GL3 getGL2GL3() {
        return this;
    }

    @Override
    public int GL_VERTEX_SHADER() {
        return com.jogamp.opengl.GL2GL3.GL_VERTEX_SHADER;
    }

    @Override
    public int GL_FRAGMENT_SHADER() {
        return com.jogamp.opengl.GL2GL3.GL_FRAGMENT_SHADER;
    }

    @Override
    public int GL_GEOMETRY_SHADER() {
        return com.jogamp.opengl.GL3ES3.GL_GEOMETRY_SHADER;
    }

    public void glUseProgram(int program) {
        ggl().glUseProgram(program);
    }

    public void glDeleteProgram(int program) {
        ggl().glDeleteProgram(program);
    }

    public void glUniformMatrix4fv(int location, int i, boolean transpose, float[] value, int i1) {
        ggl().glUniformMatrix4fv(location, i, transpose, value, i1);
    }

    public int glCreateProgram() {
        return ggl().glCreateProgram();
    }

    public void glAttachShader(int program, int vs) {
        ggl().glAttachShader(program, vs);
    }

    public void glLinkProgram(int program) {
        ggl().glLinkProgram(program);
    }

    public void glValidateProgram(int program) {
        ggl().glValidateProgram(program);
    }

    public void glDeleteShader(int vs) {
        ggl().glDeleteShader(vs);
    }

    public void glGenBuffers(int[] handles) {
        ggl().glGenBuffers(handles.length, handles, 0);
    }

    public int GL_ARRAY_BUFFER() {
        return com.jogamp.opengl.GL2GL3.GL_ARRAY_BUFFER;
    }

    public int GL_STREAM_DRAW() {
        return com.jogamp.opengl.GL2GL3.GL_STREAM_DRAW;
    }

    public void glBindBuffer(int gl_array_buffer, int vbo) {
        ggl().glBindBuffer(gl_array_buffer, vbo);
    }

    public void glBufferData(int gl_array_buffer, int size, FloatBuffer o, int gl_stream_draw) {
        ggl().glBufferData(gl_array_buffer, size, o, gl_stream_draw);
    }

    public int GL_VALIDATE_STATUS() {
        return com.jogamp.opengl.GL2GL3.GL_VALIDATE_STATUS;
    }

    public int GL_LINK_STATUS() {
        return com.jogamp.opengl.GL2GL3.GL_LINK_STATUS;
    }

    public int GL_COMPILE_STATUS() {
        return com.jogamp.opengl.GL2GL3.GL_COMPILE_STATUS;
    }

    public int glCreateShader(int type) {
        return ggl().glCreateShader(type);
    }

    public void glShaderSource(int shader, String string) {
        String[] strings = new String[]{string};
        ggl().glShaderSource(shader, 1, strings, null);
    }

    public void glCompileShader(int shader) {
        ggl().glCompileShader(shader);
    }

    public int GL_VERTEX_ARRAY() {
        return com.jogamp.opengl.GLES2.GL_VERTEX_ARRAY;
    }

    public int GL_TEXTURE_COORD_ARRAY() {
        return com.jogamp.opengl.GL2ES1.GL_TEXTURE_COORD_ARRAY;
    }

    public void glDeleteBuffers(int[] handles) {
        ggl().glDeleteBuffers(handles.length, handles, 0);
    }

    public void glBufferSubData(int gl_array_buffer, int i, int sizeInBytes, FloatBuffer data) {
        ggl().glBufferSubData(gl_array_buffer, i, sizeInBytes, data);
    }

    public void glDrawArrays(int gl_quads, int i, int sizeInVertices) {
        ggl().glDrawArrays(gl_quads, i, sizeInVertices);
    }

    public void glEnableVertexAttribArray(int coordLoc) {
        ggl().glEnableVertexAttribArray(coordLoc);
    }

    public int glGetAttribLocation(int program, String coordAttribName) {
        return ggl().glGetAttribLocation(program, coordAttribName);
    }

    public void glBindVertexArray(int i) {
        ggl().glBindVertexArray(i);
    }

    public void glVertexAttribPointer(int coordLoc, int floatsPerCoord, int gl_float, boolean b, int stride, int coordOffset) {
        ggl().glVertexAttribPointer(coordLoc, floatsPerCoord, gl_float, b, stride, coordOffset);
    }

    public void glGenVertexArrays(int[] handles) {
        ggl().glGenVertexArrays(handles.length, handles, 0);
    }

    public void glDisableVertexAttribArray(int vertCoordLocation) {
        ggl().glDisableVertexAttribArray(vertCoordLocation);
    }

    public void glDeleteVertexArrays(int[] handles) {
        ggl().glDeleteVertexArrays(handles.length, handles, 0);
    }

    @Override
    public int glGetUniformLocation(int program, String name) {
        return ggl().glGetUniformLocation(program, name);
    }

    @Override
    public void glUniform4fv(int location, int i, float[] value, int i1) {
        ggl().glUniform4fv(location, i, value, i1);
    }

    @Override
    public int GL_INFO_LOG_LENGTH() {
        return com.jogamp.opengl.GL2ES2.GL_INFO_LOG_LENGTH;
    }

    @Override
    public void glGetShaderiv(int shaderObj, int gl_info_log_length, int[] infoLogLength, int i) {
        ggl().glGetShaderiv(shaderObj, gl_info_log_length, infoLogLength, i);
    }

    @Override
    public String glGetShaderInfoLog(int shaderObj) {

        final int[] infoLogLength=new int[1];
        ggl().glGetShaderiv(shaderObj, com.jogamp.opengl.GL2ES2.GL_INFO_LOG_LENGTH, infoLogLength, 0);

        if(infoLogLength[0]==0) {
            return "(no info log)";
        } else {
            final int[] charsWritten=new int[1];
            final byte[] infoLogBytes = new byte[infoLogLength[0]];
            ggl().glGetShaderInfoLog(shaderObj, infoLogLength[0], charsWritten, 0, infoLogBytes, 0);

            return new String(infoLogBytes, 0, charsWritten[0]);
        }
    }

    @Override
    public void glGetProgramiv(int programObj, int gl_info_log_length, int[] infoLogLength, int i) {
        ggl().glGetProgramiv(programObj, gl_info_log_length, infoLogLength, i);
    }

    @Override
    public String glGetProgramInfoLog(int programObj) {
        final int[] infoLogLength=new int[1];
        ggl().glGetProgramiv(programObj, com.jogamp.opengl.GL2ES2.GL_INFO_LOG_LENGTH, infoLogLength, 0);

        if(infoLogLength[0]==0) {
            return "(no info log)";
        } else {
            final int[] charsWritten = new int[1];
            final byte[] infoLogBytes = new byte[infoLogLength[0]];
            ggl().glGetProgramInfoLog(programObj, infoLogLength[0], charsWritten, 0, infoLogBytes, 0);

            return new String(infoLogBytes, 0, charsWritten[0]);
        }

    }

    @Override
    public boolean glIsProgram(int programObj) {
        return ggl().glIsProgram(programObj);
    }

    @Override
    public void glDetachShader(int program, int i) {
        ggl().glDetachShader(program, i);
    }

    @Override
    public boolean glIsBuffer(int bufferId) {
        return ggl().glIsBuffer(bufferId);
    }


    @Override
    public void glUniform1i(int textureLocation, int unit) {
        ggl().glUniform1i(textureLocation, unit);
    }

    @Override
    public void glUniform1f(int lineWidthLocation, float lineWidth) {
        ggl().glUniform1f(lineWidthLocation, lineWidth);
    }

    @Override
    public int GL_GEOMETRY_INPUT_TYPE() {
        return com.jogamp.opengl.GL3ES3.GL_GEOMETRY_INPUT_TYPE;
    }

    @Override
    public int GL_GEOMETRY_OUTPUT_TYPE() {
        return com.jogamp.opengl.GL3ES3.GL_GEOMETRY_OUTPUT_TYPE;
    }

    @Override
    public int GL_GEOMETRY_VERTICES_OUT() {
        return com.jogamp.opengl.GL3ES3.GL_GEOMETRY_OUTPUT_TYPE;
    }

    @Override
    public void glProgramParameteri(int programId, int gl_geometry_input_type, int gl_lines) {
        ggl().glProgramParameteri(programId, gl_geometry_input_type, gl_lines);
    }

    @Override
    public void glPointSize(float glLineWidth) {
        ggl().glPointSize(glLineWidth);
    }


}
