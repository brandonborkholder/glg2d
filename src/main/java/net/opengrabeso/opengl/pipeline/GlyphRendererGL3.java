/*
 * Copyright 2012 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */
package net.opengrabeso.opengl.pipeline;

import com.github.opengrabeso.jaagl.GL2GL3;


/**
 * Utility for drawing glyphs with OpenGL 3.
 */
/*@VisibleForTesting*/
/*@NotThreadSafe*/
public final class GlyphRendererGL3 extends AbstractGlyphRenderer {

    /**
     * Source code of vertex shader.
     */
    /*@Nonnull*/
    private static final String VERT_SOURCE =
        "#version 120\n" +
        "uniform mat4 MVPMatrix;\n" +
        "attribute vec4 MCVertex;\n" +
        "attribute vec2 TexCoord0;\n" +
        "varying vec2 Coord0;\n" +
        "void main() {\n" +
        "   gl_Position = MVPMatrix * MCVertex;\n" +
        "   Coord0 = TexCoord0;\n" +
        "}\n";

    /**
     * Source code of fragment shader.
     */
    /*@Nonnull*/
    private static final String FRAG_SOURCE =
        "#version 120\n" +
        "uniform sampler2D Texture;\n" +
        "uniform vec4 Color=vec4(1,1,1,1);\n" +
        "varying vec2 Coord0;\n" +
        "void main() {\n" +
        "   float sample;\n" +
        "   sample = texture(Texture,Coord0).r;\n" +
        "   gl_FragColor = Color * sample;\n" +
        "}\n";

    /**
     * True if blending needs to be reset.
     */
    private boolean restoreBlending;

    private boolean restoreScissor;

    /**
     * True if depth test needs to be reset.
     */
    private boolean restoreDepthTest;

    /**
     * Shader program.
     */
    /*@Nonnegative*/
    private final int program;

    /**
     * Uniform for modelview projection.
     */
    /*@Nonnull*/
    private final Mat4Uniform transform;

    /**
     * Uniform for color of glyphs.
     */
    /*@Nonnull*/
    private final Vec4Uniform color;

    /**
     * Width of last orthographic render.
     */
    /*@Nonnegative*/
    private int lastWidth = 0;

    /**
     * Height of last orthographic render
     */
    /*@Nonnegative*/
    private int lastHeight = 0;

    /**
     * Constructs a {@link GlyphRendererGL3}.
     *
     * @param gl Current OpenGL context
     * @throws NullPointerException if context is null
     */
    /*@VisibleForTesting*/
    public GlyphRendererGL3(/*@Nonnull*/ final GL2GL3 gl) {

        this.program = ShaderLoader.loadProgram(gl, VERT_SOURCE, FRAG_SOURCE);
        this.transform = new Mat4Uniform(gl, program, "MVPMatrix");
        this.color = new Vec4Uniform(gl, program, "Color");
    }

    @Override
    protected void doBeginRendering(/*@Nonnull*/ final GL2GL3 gl,
            /*@Nonnegative*/
            /*@Nonnegative*/
                                                 final boolean disableDepthTest) {

        // Activate program
        gl.glUseProgram(program);

        // Check blending and depth test
        restoreBlending = false;
        if (!gl.glIsEnabled(gl.GL_BLEND())) {
            gl.glEnable(gl.GL_BLEND());
            gl.glBlendFunc(gl.GL_ONE(), gl.GL_ONE_MINUS_SRC_ALPHA());
            restoreBlending = true;
        }
        restoreDepthTest = false;
        if (disableDepthTest && gl.glIsEnabled(gl.GL_DEPTH_TEST())) {
            gl.glDisable(gl.GL_DEPTH_TEST());
            restoreDepthTest = true;
        }
        restoreScissor = false;
        if (gl.glIsEnabled(gl.GL_SCISSOR_TEST())) {
            gl.glDisable(gl.GL_SCISSOR_TEST());
            restoreScissor = true;
        }
    }

    @Override
    protected QuadPipeline doCreateQuadPipeline(/*@Nonnull*/ final GL2GL3 gl) {

        return new QuadPipelineGL30(gl, program);
    }

    protected void doDispose(/*@Nonnull*/ final GL2GL3 gl) {
        gl.glUseProgram(0);
        gl.glDeleteProgram(program);
    }

    @Override
    protected void doEndRendering(/*@Nonnull*/ final GL2GL3 gl) {
        // Deactivate program
        gl.glUseProgram(0);

        // Check blending and depth test
        if (restoreBlending) {
            gl.glDisable(gl.GL_BLEND());
        }
        if (restoreScissor) {
            gl.glEnable(gl.GL_SCISSOR_TEST());
        }
        if (restoreDepthTest) {
            gl.glEnable(gl.GL_DEPTH_TEST());
        }
    }

    @Override
    protected void doSetColor(/*@Nonnull*/ final GL2GL3 gl,
                              final float r,
                              final float g,
                              final float b,
                              final float a) {

        color.value[0] = r;
        color.value[1] = g;
        color.value[2] = b;
        color.value[3] = a;
        color.update(gl);
    }

    @Override
    protected void doSetTransform3d(/*@Nonnull*/ final GL2GL3 gl,
                                    /*@Nonnull*/ final float[] value,
                                    final boolean transpose) {
        if (transform.location >= 0) {
            gl.glUniformMatrix4fv(transform.location, 1, transpose, value, 0);
        }
        transform.dirty = true;
    }

    @Override
    public boolean getUseVertexArrays() {
        return true;
    }

    @Override
    public void setUseVertexArrays(final boolean useVertexArrays) {
        // empty
    }
}
