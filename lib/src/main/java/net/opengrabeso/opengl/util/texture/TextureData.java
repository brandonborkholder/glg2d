/*
 * Copyright (c) 2005 Sun Microsystems, Inc. All Rights Reserved.
 * Copyright (c) 2010 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

package net.opengrabeso.opengl.util.texture;

import java.nio.ByteBuffer;

import net.opengrabeso.opengl.util.GLPixelAttributes;

/**
 * Represents the data for an OpenGL texture. This is separated from
 * the notion of a Texture to support things like streaming in of
 * textures in a background thread without requiring an OpenGL context
 * to be current on that thread.
 *
 * @author Chris Campbell
 * @author Kenneth Russell
 * @author Sven Gothel
 */

public class TextureData {
    protected int width;
    protected int height;
    private int border;
    protected GLPixelAttributes pixelAttributes;
    protected int internalFormat; // perhaps inferred from pixelFormat?
    protected boolean mustFlipVertically; // Must flip texture coordinates
    // vertically to get OpenGL output
    // to look correct
    protected ByteBuffer buffer; // the actual data...
    private Flusher flusher;
    protected int rowLength;
    protected int alignment; // 1, 2, or 4 bytes

    /** Used only by subclasses */
    protected TextureData() { this.pixelAttributes = GLPixelAttributes.UNDEF; }

    public TextureData(final int internalFormat,
                       final int width,
                       final int height,
                       final int border,
                       final int dataFormat,
                       final int dataType,
                       final boolean dataIsCompressed,
                       final boolean mustFlipVertically,
                       final ByteBuffer buffer,
                       final Flusher flusher) throws IllegalArgumentException {

        this.width = width;
        this.height = height;
        this.border = border;
        this.pixelAttributes = new GLPixelAttributes(dataFormat, dataType);
        this.internalFormat = internalFormat;
        this.mustFlipVertically = mustFlipVertically;
        this.buffer = buffer;
        this.flusher = flusher;
        alignment = 1;  // FIXME: is this correct enough in all situations?
    }

    /** Returns the width in pixels of the texture data. */
    public int getWidth() { return width; }
    /** Returns the height in pixels of the texture data. */
    public int getHeight() { return height; }
    /** Returns the border in pixels of the texture data. */
    public int getBorder() {
        return border;
    }
    /** Returns the intended OpenGL {@link GLPixelAttributes} of the texture data, i.e. format and type. */
    public GLPixelAttributes getPixelAttributes() {
        return pixelAttributes;
    }
    /** Returns the intended OpenGL pixel format of the texture data using {@link #getPixelAttributes()}. */
    public int getPixelFormat() {
        return pixelAttributes.format;
    }
    /** Returns the intended OpenGL pixel type of the texture data using {@link #getPixelAttributes()}. */
    public int getPixelType() {
        return pixelAttributes.type;
    }
    /** Returns the intended OpenGL internal format of the texture data. */
    public int getInternalFormat() {
        return internalFormat;
    }

    /** Indicates whether the texture coordinates must be flipped
        vertically for proper display. */
    public boolean getMustFlipVertically() {
        return mustFlipVertically;
    }
    /** Returns the texture data, or null if it is specified as a set of mipmaps. */
    public ByteBuffer getBuffer() {
        return buffer;
    }
    /** Returns the required byte alignment for the texture data. */
    public int getAlignment() {
        return alignment;
    }
    /** Returns the row length needed for correct GL_UNPACK_ROW_LENGTH
        specification. This is currently only supported for
        non-mipmapped, non-compressed textures. */
    public int getRowLength() {
        return rowLength;
    }

    /** Sets the width in pixels of the texture data. */
    public void setWidth(final int width) { this.width = width; }
    /** Sets the height in pixels of the texture data. */
    public void setHeight(final int height) { this.height = height; }
    /** Sets the border in pixels of the texture data. */
    public void setBorder(final int border) { this.border = border; }
    /** Sets the intended OpenGL pixel format of the texture data. */
    public void setPixelAttributes(final GLPixelAttributes pixelAttributes) { this.pixelAttributes = pixelAttributes; }
    /**
     * Sets the intended OpenGL pixel format component of {@link GLPixelAttributes} of the texture data.
     * <p>
     * Use {@link #setPixelAttributes(GLPixelAttributes)}, if setting format and type.
     * </p>
     */
    public void setPixelFormat(final int pixelFormat) {
        if( pixelAttributes.format != pixelFormat ) {
            pixelAttributes = new GLPixelAttributes(pixelFormat, pixelAttributes.type);
        }
    }
    /**
     * Sets the intended OpenGL pixel type component of {@link GLPixelAttributes} of the texture data.
     * <p>
     * Use {@link #setPixelAttributes(GLPixelAttributes)}, if setting format and type.
     * </p>
     */
    public void setPixelType(final int pixelType) {
        if( pixelAttributes.type != pixelType) {
            pixelAttributes = new GLPixelAttributes(pixelAttributes.format, pixelType);
        }
    }
    /** Sets whether the texture coordinates must be flipped vertically
        for proper display. */
    public void setMustFlipVertically(final boolean mustFlipVertically) { this.mustFlipVertically = mustFlipVertically; }
    /** Sets the texture data. */
    public void setBuffer(final ByteBuffer buffer) {
        this.buffer = buffer;
    }
    /** Sets the required byte alignment for the texture data. */
    public void setAlignment(final int alignment) { this.alignment = alignment; }
    /** Sets the row length needed for correct GL_UNPACK_ROW_LENGTH
        specification. This is currently only supported for
        non-mipmapped, non-compressed textures. */
    public void setRowLength(final int rowLength) { this.rowLength = rowLength; }

    /** Flushes resources associated with this TextureData by calling
        Flusher.flush(). */
    public void flush() {
        if (flusher != null) {
            flusher.flush();
            flusher = null;
        }
    }

    /** Calls flush()
     * @see #flush()
     */
    public void destroy() {
        flush();
    }

    /** Defines a callback mechanism to allow the user to explicitly
        deallocate native resources (memory-mapped files, etc.)
        associated with a particular TextureData. */
    public static interface Flusher {
        /** Flushes any native resources associated with this
            TextureData. */
        public void flush();
    }

    @Override
    public String toString() {
        return "TextureData["+width+"x"+height+", y-flip "+mustFlipVertically+", internFormat 0x"+Integer.toHexString(internalFormat)+", "+
                pixelAttributes+", border "+border+", alignment "+alignment+", rowlen "+rowLength+ "";
    }

}
