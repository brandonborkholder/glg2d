package glg2d.impl.shader;

import glg2d.impl.AbstractColorHelper;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;

public class GL2ES2ColorHelper extends AbstractColorHelper {
  protected float[] foregroundRGBA = new float[4];

  @Override
  public void setColorNoRespectComposite(Color c) {
    foregroundRGBA[0] = c.getRed() / 255f;
    foregroundRGBA[1] = c.getGreen() / 255f;
    foregroundRGBA[2] = c.getBlue() / 255f;
    foregroundRGBA[3] = c.getAlpha() / 255f;
  }

  @Override
  public void setColorRespectComposite(Color c) {
    float alpha = getCompositeAlpha();
    foregroundRGBA[0] = c.getRed() / 255f;
    foregroundRGBA[1] = c.getGreen() / 255f;
    foregroundRGBA[2] = c.getBlue() / 255f;
    foregroundRGBA[3] = (c.getAlpha() / 255f) * alpha;
  }

  @Override
  public void setPaintMode() {
    // not implemented yet
  }

  @Override
  public void setXORMode(Color c) {
    // not implemented yet
  }

  @Override
  public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    // not implemented yet
  }

  public float[] getForegroundRGBA() {
    return foregroundRGBA;
  }

  public float getCompositeAlpha() {
    Composite composite = getComposite();
    float alpha = 1;
    if (composite instanceof AlphaComposite) {
      alpha = ((AlphaComposite) composite).getAlpha();
    }

    return alpha;
  }
}
