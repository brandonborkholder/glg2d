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
import com.github.opengrabeso.jaagl.GL2;

/**
 * {@link QuadPipeline} for use with OpenGL 1.5.
 */
/*@VisibleForTesting*/
/*@NotThreadSafe*/
public final class QuadPipelineGL15 extends AbstractQuadPipeline {

    /**
     * Number of vertices per primitive.
     */
    /*@Nonnegative*/
    private static final int VERTS_PER_PRIM = 4;

    /**
     * Number of primitives per quad.
     */
    /*@Nonnegative*/
    private static final int PRIMS_PER_QUAD = 1;

    /**
     * OpenGL handle to vertex buffer.
     */
    /*@Nonnegative*/
    private final int vbo;

    /**
     * Constructs a {@link QuadPipelineGL15}.
     *
     * @param gl Current OpenGL context
     * @throws NullPointerException if context is null
     */
    /*@VisibleForTesting*/
    public QuadPipelineGL15(/*@Nonnull*/ final GL2 gl) {

        super(VERTS_PER_PRIM, PRIMS_PER_QUAD);

        this.vbo = createVertexBufferObject(gl, BYTES_PER_BUFFER);
    }

    @Override
    public void beginRendering(/*@Nonnull*/ final GL2GL3 gl) {

        super.beginRendering(gl);

        final GL2 gl2 = gl.getGL2();

        // Change state
        gl2.glPushClientAttrib((int) gl2.GL_ALL_CLIENT_ATTRIB_BITS());
        gl2.glBindBuffer(gl2.GL_ARRAY_BUFFER(), vbo);

        // Points
        gl2.glEnableClientState(gl2.GL_VERTEX_ARRAY());
        gl2.glVertexPointer(
                FLOATS_PER_POINT,   // size
                gl2.GL_FLOAT(),       // type
                STRIDE,             // stride
                POINT_OFFSET);      // offset

        // Coordinates
        gl2.glEnableClientState(gl2.GL_TEXTURE_COORD_ARRAY());
        gl2.glTexCoordPointer(
                FLOATS_PER_COORD,   // size
                gl2.GL_FLOAT(),       // type
                STRIDE,             // stride
                COORD_OFFSET);      // offset
    }

    @Override
    public void dispose(/*@Nonnull*/ final GL2GL3 gl) {

        super.dispose(gl);

        final GL2 gl2 = gl.getGL2();

        // Delete the vertex buffer object
        final int[] handles = new int[] { vbo };
        gl2.glDeleteBuffers(handles);
    }

    @Override
    protected void doFlush(/*@Nonnull*/ final GL2GL3 gl) {

        final GL2 gl2 = gl.getGL2();

        // Upload data
        rewind();
        gl2.glBufferSubData(
                gl2.GL_ARRAY_BUFFER(), // target
                0,                   // offset
                getSizeInBytes(),    // size
                getData());          // data

        // Draw
        gl2.glDrawArrays(
                gl2.GL_QUADS(),         // mode
                0,                    // first
                getSizeInVertices()); // count

        clear();
    }

    @Override
    public void endRendering(/*@Nonnull*/ final GL2GL3 gl) {

        super.endRendering(gl);

        final GL2 gl2 = gl.getGL2();

        // Restore state
        gl2.glBindBuffer(gl2.GL_ARRAY_BUFFER(), 0);
        gl2.glPopClientAttrib();
    }
}
