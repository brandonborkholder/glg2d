package net.opengrabeso.opengl.util.awt;

import com.github.opengrabeso.jaagl.GL2;
import com.jogamp.common.nio.Buffers;

import java.nio.FloatBuffer;

public abstract class Pipelined_QuadRenderer {
    private final GL2 gl;
    int mOutstandingGlyphsVerticesPipeline = 0;
    FloatBuffer mTexCoords;
    FloatBuffer mVertCoords;
    int mVBO_For_ResuableTileVertices;
    int mVBO_For_ResuableTileTexCoords;

    protected abstract void uploadTexture();

    public Pipelined_QuadRenderer(GL2 gl) {
        mVertCoords = Buffers.newDirectFloatBuffer(TextRenderer.kTotalBufferSizeCoordsVerts);
        mTexCoords = Buffers.newDirectFloatBuffer(TextRenderer.kTotalBufferSizeCoordsTex);
        this.gl = gl;

        final int[] vbos = new int[2];
        gl.glGenBuffers(vbos);

        mVBO_For_ResuableTileVertices = vbos[0];
        mVBO_For_ResuableTileTexCoords = vbos[1];

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER(),
                mVBO_For_ResuableTileVertices);
        gl.glBufferData(gl.GL_ARRAY_BUFFER(), TextRenderer.kTotalBufferSizeBytesVerts,
                null, gl.GL_STREAM_DRAW()); // stream draw because this is a single quad use pipeline

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER(),
                mVBO_For_ResuableTileTexCoords);
        gl.glBufferData(gl.GL_ARRAY_BUFFER(), TextRenderer.kTotalBufferSizeBytesTex,
                null, gl.GL_STREAM_DRAW()); // stream draw because this is a single quad use pipeline
    }

    public void glTexCoord2f(final float v, final float v1) {
        mTexCoords.put(v);
        mTexCoords.put(v1);
    }

    public void glVertex3f(final float inX, final float inY, final float inZ) {
        mVertCoords.put(inX);
        mVertCoords.put(inY);
        mVertCoords.put(inZ);

        mOutstandingGlyphsVerticesPipeline++;

        if (mOutstandingGlyphsVerticesPipeline >= TextRenderer.kTotalBufferSizeVerts) {
            this.draw();
        }
    }

    public void draw() {
        drawVertexArrays();
    }

    private void drawVertexArrays() {
        if (mOutstandingGlyphsVerticesPipeline > 0) {
            uploadTexture();

            mVertCoords.rewind();
            mTexCoords.rewind();

            gl.glEnableClientState(gl.GL_VERTEX_ARRAY());

            gl.glBindBuffer(gl.GL_ARRAY_BUFFER(), mVBO_For_ResuableTileVertices);
            gl.glBufferSubData(gl.GL_ARRAY_BUFFER(), 0,
                    mOutstandingGlyphsVerticesPipeline * TextRenderer.kSizeInBytes_OneVertices_VertexData,
                    mVertCoords); // upload only the new stuff
            gl.glVertexPointer(3, gl.GL_FLOAT(), 0, 0);

            gl.glEnableClientState(gl.GL_TEXTURE_COORD_ARRAY());

            gl.glBindBuffer(gl.GL_ARRAY_BUFFER(), mVBO_For_ResuableTileTexCoords);
            gl.glBufferSubData(gl.GL_ARRAY_BUFFER(), 0,
                    mOutstandingGlyphsVerticesPipeline * TextRenderer.kSizeInBytes_OneVertices_TexData,
                    mTexCoords); // upload only the new stuff
            gl.glTexCoordPointer(2, gl.GL_FLOAT(), 0, 0);

            gl.glDrawArrays(gl.GL_QUADS(), 0, mOutstandingGlyphsVerticesPipeline);

            mVertCoords.rewind();
            mTexCoords.rewind();
            mOutstandingGlyphsVerticesPipeline = 0;
        }
    }

    public void dispose() {
        final int[] vbos = new int[2];
        vbos[0] = mVBO_For_ResuableTileVertices;
        vbos[1] = mVBO_For_ResuableTileTexCoords;
        gl.glDeleteBuffers(vbos);
    }
}
