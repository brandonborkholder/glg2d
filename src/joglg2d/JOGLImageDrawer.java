package joglg2d;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;

/**
 * @author borkholder
 * @created Apr 27, 2010
 *
 */
public class JOGLImageDrawer {
  private static final AffineTransform IDENTITY = new AffineTransform();

  private final GL gl;

  public JOGLImageDrawer(GL gl) {
    this.gl = gl;
  }

  public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
    if (xform == null) {
      xform = IDENTITY;
    }

    int width = img.getWidth(null);
    int height = img.getHeight(null);
    WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, 4, null);
    ComponentColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 }, true,
        false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
    BufferedImage renderedImg = new BufferedImage(colorModel, raster, false, null);

    Graphics2D g2d = renderedImg.createGraphics();
    AffineTransform transform = AffineTransform.getTranslateInstance(0, height);
    transform.scale(1, -1);

    g2d.setTransform(transform);
    g2d.drawImage(img, null, null);

    Point2D.Float pt = new Point2D.Float(0, height);
    xform.transform(pt, pt);

    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    gl.glEnable(GL.GL_BLEND);
    DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
    gl.glRasterPos2f(pt.x, pt.y);
    gl.glDrawPixels(width, height, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap(buffer.getData()));
    return true;
  }
}
