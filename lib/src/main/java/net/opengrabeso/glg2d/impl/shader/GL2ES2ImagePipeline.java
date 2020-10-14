/*
 * Copyright 2015 Brandon Borkholder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.opengrabeso.glg2d.impl.shader;

import static net.opengrabeso.glg2d.GLG2DUtils.ensureIsGLBuffer;

import java.nio.FloatBuffer;

import com.github.opengrabeso.jaagl.GL2GL3;

import com.jogamp.common.nio.Buffers;

public class GL2ES2ImagePipeline extends AbstractShaderPipeline {
    protected int vertexBufferId = -1;
    protected int vertexArrayId = -1;

    protected int textureLocation = -1;
    protected int vertCoordLocation = -1;
    protected int texCoordLocation = -1;

    public GL2ES2ImagePipeline() {
        this("TextureShader.v", "TextureShader.f");
    }

    public GL2ES2ImagePipeline(String vertexShaderFileName, String fragmentShaderFileName) {
        super(vertexShaderFileName, null, fragmentShaderFileName);
    }

    public void setTextureUnit(GL2GL3 gl, int unit) {
        if (textureLocation >= 0) {
            gl.glUniform1i(textureLocation, unit);
        }
    }

    protected void bufferData(GL2GL3 gl, FloatBuffer buffer) {
        if (vertexArrayId < 0) {
            int[] vao = new int[]{0};
            gl.glGenVertexArrays(vao);
            vertexArrayId = vao[0];
        }

        gl.glBindVertexArray(vertexArrayId);

        // TODO: no need to enable / disable, this is bound to VAO permanently
        vertexBufferId = ensureIsGLBuffer(gl, vertexBufferId);

        gl.glEnableVertexAttribArray(vertCoordLocation);
        gl.glEnableVertexAttribArray(texCoordLocation);

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER(), vertexBufferId);
        gl.glBufferData(gl.GL_ARRAY_BUFFER(), Buffers.SIZEOF_FLOAT * 16, buffer, gl.GL_STATIC_DRAW());

        gl.glVertexAttribPointer(vertCoordLocation, 2, gl.GL_FLOAT(), false, 4 * Buffers.SIZEOF_FLOAT, 0);
        gl.glVertexAttribPointer(texCoordLocation, 2, gl.GL_FLOAT(), false, 4 * Buffers.SIZEOF_FLOAT, 2 * Buffers.SIZEOF_FLOAT);
    }

    public void draw(GL2GL3 gl, FloatBuffer interleavedVertTexBuffer) {
        bufferData(gl, interleavedVertTexBuffer);

        gl.glDrawArrays(gl.GL_TRIANGLE_STRIP(), 0, 4);

        gl.glDisableVertexAttribArray(vertCoordLocation);
        gl.glDisableVertexAttribArray(texCoordLocation);
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER(), 0);
        gl.glBindVertexArray(0);
    }

    @Override
    protected void setupUniformsAndAttributes(GL2GL3 gl) {
        super.setupUniformsAndAttributes(gl);

        transformLocation = gl.glGetUniformLocation(programId, "u_transform");
        colorLocation = gl.glGetUniformLocation(programId, "u_color");
        textureLocation = gl.glGetUniformLocation(programId, "u_tex");

        vertCoordLocation = gl.glGetAttribLocation(programId, "a_vertCoord");
        texCoordLocation = gl.glGetAttribLocation(programId, "a_texCoord");
    }

    @Override
    public void delete(GL2GL3 gl) {
        super.delete(gl);

        if (gl.glIsBuffer(vertexBufferId)) {
            gl.glDeleteBuffers(new int[]{vertexBufferId});
        }
        if (vertexArrayId >= 0) {
            gl.glDeleteVertexArrays(new int[]{vertexArrayId});
        }
    }
}
