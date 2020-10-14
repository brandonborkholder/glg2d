package com.github.opengrabeso.jaagl;

import java.nio.FloatBuffer;

public interface GL2GL3 extends GL {
    void glUseProgram(int program);

    void glDeleteProgram(int program);

    void glUniformMatrix4fv(int location, int i, boolean transpose, float[] value, int i1);

    int glCreateProgram();

    void glAttachShader(int program, int vs);

    void glLinkProgram(int program);

    void glValidateProgram(int program);

    void glDeleteShader(int vs);

    int GL_VERTEX_SHADER();

    int GL_FRAGMENT_SHADER();

    int GL_GEOMETRY_SHADER();

    void glGenBuffers(int[] handles);

    int GL_ARRAY_BUFFER();

    int GL_STREAM_DRAW();

    void glBindBuffer(int gl_array_buffer, int vbo);

    void glBufferData(int gl_array_buffer, int size, FloatBuffer o, int gl_stream_draw);

    int GL_VALIDATE_STATUS();

    int GL_LINK_STATUS();

    int GL_COMPILE_STATUS();

    int glCreateShader(int type);

    void glShaderSource(int shader, String strings);

    void glCompileShader(int shader);

    int GL_VERTEX_ARRAY();

    int GL_TEXTURE_COORD_ARRAY();

    void glDeleteBuffers(int[] handles);

    void glBufferSubData(int gl_array_buffer, int i, int sizeInBytes, FloatBuffer data);

    void glDrawArrays(int gl_quads, int i, int sizeInVertices);

    void glEnableVertexAttribArray(int coordLoc);

    int glGetAttribLocation(int program, String coordAttribName);

    void glBindVertexArray(int i);

    void glVertexAttribPointer(int coordLoc, int floatsPerCoord, int gl_float, boolean b, int stride, int coordOffset);

    void glGenVertexArrays(int[] handles);

    void glDeleteVertexArrays(int[] handles);

    int glGetUniformLocation(int program, String name);

    void glUniform4fv(int location, int i, float[] value, int i1);

    int GL_INFO_LOG_LENGTH();

    void glGetShaderiv(int shaderObj, int gl_info_log_length, int[] infoLogLength, int i);

    String glGetShaderInfoLog(int shaderObj);

    void glGetProgramiv(int programObj, int gl_info_log_length, int[] infoLogLength, int i);

    String glGetProgramInfoLog(int programObj);

    boolean glIsProgram(int programObj);

    void glDetachShader(int program, int i);

    boolean glIsBuffer(int bufferId);

    void glDisableVertexAttribArray(int vertCoordLocation);

    void glUniform1i(int textureLocation, int unit);

    void glUniform1f(int lineWidthLocation, float lineWidth);

    int GL_GEOMETRY_INPUT_TYPE();

    int GL_GEOMETRY_OUTPUT_TYPE();

    int GL_GEOMETRY_VERTICES_OUT();

    void glProgramParameteri(int programId, int gl_geometry_input_type, int gl_lines);
}
