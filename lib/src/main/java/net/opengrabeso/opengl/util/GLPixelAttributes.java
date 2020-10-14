package net.opengrabeso.opengl.util;

/**
 * Pixel attributes.
 */
public class GLPixelAttributes {
    /**
     * Undefined instance of {@link GLPixelAttributes}, having componentCount:=0, format:=0 and type:= 0.
     */
    public static final GLPixelAttributes UNDEF = new GLPixelAttributes(0, 0);

    /**
     * The OpenGL pixel data format
     */
    public final int format;
    /**
     * The OpenGL pixel data type
     */
    public final int type;

    @Override
    public final int hashCode() {
        // 31 * x == (x << 5) - x
        int hash = format;
        return ((hash << 5) - hash) + type;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GLPixelAttributes) {
            final GLPixelAttributes other = (GLPixelAttributes) obj;
            return format == other.format &&
                    type == other.type;
        } else {
            return false;
        }
    }

    /**
     * Create a new {@link GLPixelAttributes} instance based on GL format and type.
     *
     * @param dataFormat GL data format
     * @param dataType   GL data type
     */
    public GLPixelAttributes(final int dataFormat, final int dataType) {
        this.format = dataFormat;
        this.type = dataType;
    }

    @Override
    public String toString() {
        return "PixelAttributes[fmt 0x" + Integer.toHexString(format) + ", type 0x" + Integer.toHexString(type) + ", " + "]";
    }
}
