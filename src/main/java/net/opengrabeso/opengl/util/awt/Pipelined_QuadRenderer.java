package net.opengrabeso.opengl.util.awt;

import com.github.opengrabeso.jaagl.GL2GL3;
import com.jogamp.common.nio.Buffers;
import net.opengrabeso.opengl.util.texture.TextureCoords;

import java.nio.FloatBuffer;

public abstract class Pipelined_QuadRenderer {
    private final GL2GL3 gl;
    int mOutstandingGlyphsVerticesPipeline = 0;
    FloatBuffer mVert;
    int mVBO;

    protected abstract void uploadTexture();
    protected abstract void setupDraw();
    protected abstract void cleanupDraw();

    public Pipelined_QuadRenderer(GL2GL3 gl) {
        mVert = Buffers.newDirectFloatBuffer(TextRenderer.kTotalBufferSizeBytes);

        this.gl = gl;

        final int[] vbos = new int[1];
        gl.glGenBuffers(vbos);

        mVBO = vbos[0];

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER(), mVBO);
        gl.glBufferData(gl.GL_ARRAY_BUFFER(), TextRenderer.kTotalBufferSizeBytes,
                null, gl.GL_STREAM_DRAW()); // stream draw because this is a single quad use pipeline

    }

    private void glTexCoord2f(final float v, final float v1) {
        mVert.put(v);
        mVert.put(v1);
    }

    private void glVertex3f(final float inX, final float inY, final float inZ) {
        mVert.put(inX);
        mVert.put(inY);
        mVert.put(inZ);

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

            mVert.rewind();


            gl.glBindBuffer(gl.GL_ARRAY_BUFFER(), mVBO);
            gl.glBufferSubData(gl.GL_ARRAY_BUFFER(), 0,
                    mOutstandingGlyphsVerticesPipeline * TextRenderer.kSizeInBytes_OneVertices_VertexData,
                    mVert); // upload only the new stuff

            setupDraw();
            gl.glDrawArrays(gl.GL_TRIANGLES(), 0, mOutstandingGlyphsVerticesPipeline);
            cleanupDraw();

            mVert.rewind();
            mOutstandingGlyphsVerticesPipeline = 0;

        }
    }

    public void dispose() {
        final int[] vbos = new int[1];
        vbos[0] = mVBO;
        gl.glDeleteBuffers(vbos);
    }

    public void quad(float xx, float yy, float z, float width, float height, TextureCoords coords) {
        glVertex3f(xx, yy, z);
        glTexCoord2f(coords.left(), coords.bottom());
        glVertex3f(xx + width, yy, z);
        glTexCoord2f(coords.right(), coords.bottom());
        glVertex3f(xx + width, yy + height, z);
        glTexCoord2f(coords.right(), coords.top());

        glVertex3f(xx, yy, z);
        glTexCoord2f(coords.left(), coords.bottom());
        glVertex3f(xx + width, yy + height, z);
        glTexCoord2f(coords.right(), coords.top());
        glVertex3f(xx, yy + height, z);
        glTexCoord2f(coords.left(), coords.top());
    }
}
