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

package net.opengrabeso.opengl.util.texture.awt;

import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.nio.ByteBuffer;

import com.github.opengrabeso.jaagl.GL2GL3;
import net.opengrabeso.opengl.util.GLPixelAttributes;
import net.opengrabeso.opengl.util.texture.TextureData;

public class AWTTextureData extends TextureData {

    /**
     * Constructs a new TextureData object with the specified parameters
     * and data contained in the given BufferedImage. The resulting
     * TextureData "wraps" the contents of the BufferedImage, so if a
     * modification is made to the BufferedImage between the time the
     * TextureData is constructed and when a Texture is made from the
     * TextureData, that modification will be visible in the resulting
     * Texture.
     *
     * @param internalFormat the OpenGL internal format for the
     *                       resulting texture; may be 0, in which case
     *                       it is inferred from the image's type
     * @param image          the image containing the texture data
     */
    public AWTTextureData(final GL2GL3 gl,
                          int internalFormat,
                          final BufferedImage image) {
        assert internalFormat != 0;
        this.internalFormat = internalFormat;
        createFromImage(gl, image);
    }

    private void validatePixelAttributes() {
    }

    @Override
    public GLPixelAttributes getPixelAttributes() {
        validatePixelAttributes();
        return super.getPixelAttributes();
    }

    @Override
    public int getPixelFormat() {
        validatePixelAttributes();
        return super.getPixelFormat();
    }

    @Override
    public int getPixelType() {
        validatePixelAttributes();
        return super.getPixelType();
    }

    @Override
    public ByteBuffer getBuffer() {
        return buffer;
    }

    private void createFromImage(GL2GL3 gl, final BufferedImage image) {
        pixelAttributes = GLPixelAttributes.UNDEF; // Determine from image
        mustFlipVertically = true;

        width = image.getWidth();
        height = image.getHeight();

        int scanlineStride;

        final SampleModel sm = image.getRaster().getSampleModel();
        if (sm instanceof SinglePixelPackedSampleModel) {
            scanlineStride =
                    ((SinglePixelPackedSampleModel) sm).getScanlineStride();
        } else if (sm instanceof MultiPixelPackedSampleModel) {
            scanlineStride =
                    ((MultiPixelPackedSampleModel) sm).getScanlineStride();
        } else if (sm instanceof ComponentSampleModel) {
            scanlineStride =
                    ((ComponentSampleModel) sm).getScanlineStride();
        } else {
            throw gl.newGLException("Unexpected sample model");
        }

        width = image.getWidth();
        height = image.getHeight();

        if (image.getType() == BufferedImage.TYPE_BYTE_GRAY) {
            int format = gl.isGL3() ? gl.getGL3().GL_RED() : gl.getGL2().GL_LUMINANCE();
            pixelAttributes = new GLPixelAttributes(format, gl.GL_UNSIGNED_BYTE());
            rowLength = scanlineStride;
            alignment = 1;
        } else {
            throw gl.newGLException("Unsupported image type");
        }

        createNIOBufferFromImage(image);
    }

    private void createNIOBufferFromImage(final BufferedImage image) {
        buffer = wrapImageDataBuffer(image);
    }

    private ByteBuffer wrapImageDataBuffer(final BufferedImage image) {
        //
        // Note: Grabbing the DataBuffer will defeat Java2D's image
        // management mechanism (as of JDK 5/6, at least).  This shouldn't
        // be a problem for most JOGL apps, but those that try to upload
        // the image into an OpenGL texture and then use the same image in
        // Java2D rendering might find the 2D rendering is not as fast as
        // it could be.
        //

        final DataBuffer data = image.getRaster().getDataBuffer();
        if (data instanceof DataBufferByte) {
            return ByteBuffer.wrap(((DataBufferByte) data).getData());
        } else {
            throw new RuntimeException("Unexpected DataBuffer type?");
        }
    }
}
