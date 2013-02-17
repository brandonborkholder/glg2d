package org.jogamp.glg2d.newt;

import static org.jogamp.glg2d.impl.GLG2DNotImplemented.notImplemented;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ContainerPeer;
import java.awt.peer.WindowPeer;

import javax.media.nativewindow.util.InsetsImmutable;

import sun.awt.CausedFocusEvent.Cause;
import sun.java2d.pipe.Region;

import com.jogamp.newt.Window;

@SuppressWarnings("restriction")
public class NewtPeer implements WindowPeer {
  protected final Window newtWindow;

  public NewtPeer(Window newtWindow) {
    this.newtWindow = newtWindow;
  }

  /*
   * Actually do something
   */

  @Override
  public Point getLocationOnScreen() {
    return new Point(newtWindow.getX(), newtWindow.getY());
  }

  @Override
  public Rectangle getBounds() {
    int w = newtWindow.getWidth();
    int h = newtWindow.getHeight();
    Point pt = getLocationOnScreen();
    Rectangle rect = new Rectangle(pt.x, pt.y, w, h);
    return rect;
  }

  @Override
  public Insets getInsets() {
    InsetsImmutable i = newtWindow.getInsets();
    return new Insets(i.getTopHeight(), i.getLeftWidth(), i.getBottomHeight(), i.getRightWidth());
  }

  /*
   * Implemented, but delegate
   */

  @Override
  public Insets insets() {
    return getInsets();
  }

  @Override
  public Toolkit getToolkit() {
    return Toolkit.getDefaultToolkit();
  }

  /*
   * NOT implemented
   */

  @Override
  public void reshape(int x, int y, int width, int height) {
    notImplemented("EmptyWindowPeer.reshape");
  }

  @Override
  public void setBounds(int x, int y, int width, int height, int op) {
    notImplemented("EmptyWindowPeer.setBounds");
  }

  @Override
  public void toFront() {
    notImplemented("EmptyWindowPeer.toFront");
  }

  @Override
  public void toBack() {
    notImplemented("EmptyWindowPeer.toBack");
  }

  @Override
  public void setAlwaysOnTop(boolean alwaysOnTop) {
    notImplemented("EmptyWindowPeer.setAlwaysOnTop");
  }

  @Override
  public void updateFocusableWindowState() {
    notImplemented("EmptyWindowPeer.updateFocusableWindowState");
  }

  @Override
  public void setModalBlocked(Dialog blocker, boolean blocked) {
    notImplemented("EmptyWindowPeer.setModalBlocked");
  }

  @Override
  public void updateMinimumSize() {
    notImplemented("EmptyWindowPeer.updateMinimumSize");
  }

  @Override
  public void updateIconImages() {
    notImplemented("EmptyWindowPeer.updateIconImages");
  }

  @Override
  public void setOpacity(float opacity) {
    notImplemented("EmptyWindowPeer.setOpacity");
  }

  @Override
  public void setOpaque(boolean isOpaque) {
    notImplemented("EmptyWindowPeer.setOpaque");
  }

  @Override
  public void updateWindow() {
    notImplemented("EmptyWindowPeer.updateWindow");
  }

  @Override
  public void repositionSecurityWarning() {
    notImplemented("EmptyWindowPeer.repositionSecurityWarning");
  }

  @Override
  public void beginValidate() {
    notImplemented("EmptyWindowPeer.beginValidate");
  }

  @Override
  public void endValidate() {
    notImplemented("EmptyWindowPeer.endValidate");
  }

  @Override
  public void beginLayout() {
    notImplemented("EmptyWindowPeer.beginLayout");
  }

  @Override
  public void endLayout() {
    notImplemented("EmptyWindowPeer.endLayout");
  }

  @Override
  public boolean isObscured() {
    notImplemented("EmptyWindowPeer.isObscured");
    return false;
  }

  @Override
  public boolean canDetermineObscurity() {
    notImplemented("EmptyWindowPeer.canDetermineObscurity");
    return false;
  }

  @Override
  public void setVisible(boolean v) {
    notImplemented("EmptyWindowPeer.setVisible");
  }

  @Override
  public void setEnabled(boolean e) {
    notImplemented("EmptyWindowPeer.setEnabled");
  }

  @Override
  public void paint(Graphics g) {
    notImplemented("EmptyWindowPeer.paint");
  }

  @Override
  public void print(Graphics g) {
    notImplemented("EmptyWindowPeer.print");
  }

  @Override
  public void handleEvent(AWTEvent e) {
    notImplemented("EmptyWindowPeer.handleEvent");
  }

  @Override
  public void coalescePaintEvent(PaintEvent e) {
    notImplemented("EmptyWindowPeer.coalescePaintEvent");
  }

  @Override
  public Dimension getPreferredSize() {
    notImplemented("EmptyWindowPeer.getPreferredSize");
    return null;
  }

  @Override
  public Dimension getMinimumSize() {
    notImplemented("EmptyWindowPeer.getMinimumSize");
    return null;
  }

  @Override
  public ColorModel getColorModel() {
    notImplemented("EmptyWindowPeer.getColorModel");
    return null;
  }

  @Override
  public Graphics getGraphics() {
    notImplemented("EmptyWindowPeer.getGraphics");
    return null;
  }

  @Override
  public FontMetrics getFontMetrics(Font font) {
    notImplemented("EmptyWindowPeer.getFontMetrics");
    return null;
  }

  @Override
  public void dispose() {
    notImplemented("EmptyWindowPeer.dispose");
  }

  @Override
  public void setForeground(Color c) {
    notImplemented("EmptyWindowPeer.setForeground");
  }

  @Override
  public void setBackground(Color c) {
    notImplemented("EmptyWindowPeer.setBackground");
  }

  @Override
  public void setFont(Font f) {
    notImplemented("EmptyWindowPeer.setFont");
  }

  @Override
  public void updateCursorImmediately() {
    notImplemented("EmptyWindowPeer.updateCursorImmediately");
  }

  @Override
  public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, Cause cause) {
    notImplemented("EmptyWindowPeer.requestFocus");
    return false;
  }

  @Override
  public boolean isFocusable() {
    notImplemented("EmptyWindowPeer.isFocusable");
    return false;
  }

  @Override
  public Image createImage(ImageProducer producer) {
    notImplemented("EmptyWindowPeer.createImage");
    return null;
  }

  @Override
  public Image createImage(int width, int height) {
    notImplemented("EmptyWindowPeer.createImage");
    return null;
  }

  @Override
  public VolatileImage createVolatileImage(int width, int height) {
    notImplemented("EmptyWindowPeer.createVolatileImage");
    return null;
  }

  @Override
  public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
    notImplemented("EmptyWindowPeer.prepareImage");
    return false;
  }

  @Override
  public int checkImage(Image img, int w, int h, ImageObserver o) {
    notImplemented("EmptyWindowPeer.checkImage");
    return 0;
  }

  @Override
  public GraphicsConfiguration getGraphicsConfiguration() {
    notImplemented("EmptyWindowPeer.getGraphicsConfiguration");
    return null;
  }

  @Override
  public boolean handlesWheelScrolling() {
    notImplemented("EmptyWindowPeer.handlesWheelScrolling");
    return false;
  }

  @Override
  public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {
    notImplemented("EmptyWindowPeer.createBuffers");
  }

  @Override
  public Image getBackBuffer() {
    notImplemented("EmptyWindowPeer.getBackBuffer");
    return null;
  }

  @Override
  public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction) {
    notImplemented("EmptyWindowPeer.flip");
  }

  @Override
  public void destroyBuffers() {
    notImplemented("EmptyWindowPeer.destroyBuffers");
  }

  @Override
  public void reparent(ContainerPeer newContainer) {
    notImplemented("EmptyWindowPeer.reparent");
  }

  @Override
  public boolean isReparentSupported() {
    notImplemented("EmptyWindowPeer.isReparentSupported");
    return false;
  }

  @Override
  public void layout() {
    notImplemented("EmptyWindowPeer.layout");
  }

  @Override
  public void applyShape(Region shape) {
    notImplemented("EmptyWindowPeer.applyShape");
  }

  @Override
  public boolean requestWindowFocus() {
    notImplemented("EmptyWindowPeer.requestWindowFocus");
    return false;
  }

  @Override
  public boolean isPaintPending() {
    notImplemented("EmptyWindowPeer.isPaintPending");
    return false;
  }

  @Override
  public void restack() {
    notImplemented("EmptyWindowPeer.restack");
  }

  @Override
  public boolean isRestackSupported() {
    notImplemented("EmptyWindowPeer.isRestackSupported");
    return false;
  }

  @Override
  public void repaint(long tm, int x, int y, int width, int height) {
    notImplemented("EmptyWindowPeer.repaint");
  }

  @Override
  public Dimension preferredSize() {
    notImplemented("EmptyWindowPeer.preferredSize");
    return null;
  }

  @Override
  public Dimension minimumSize() {
    notImplemented("EmptyWindowPeer.minimumSize");
    return null;
  }

  @Override
  public void show() {
    notImplemented("EmptyWindowPeer.show");
  }

  @Override
  public void hide() {
    notImplemented("EmptyWindowPeer.hide");
  }

  @Override
  public void enable() {
    notImplemented("EmptyWindowPeer.enable");
  }

  @Override
  public void disable() {
    notImplemented("EmptyWindowPeer.disable");
  }
}
