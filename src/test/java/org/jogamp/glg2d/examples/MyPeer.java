package org.jogamp.glg2d.examples;

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
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.awt.peer.FramePeer;

import javax.media.nativewindow.util.InsetsImmutable;

import sun.awt.CausedFocusEvent.Cause;
import sun.java2d.pipe.Region;

import com.jogamp.newt.opengl.GLWindow;

public class MyPeer implements FramePeer
{
	@Override
	public void toFront()
	{
    System.out.println("toFront");
	}

	@Override
	public void toBack()
	{
    System.out.println("toBack");
	}

	@Override
	public void setAlwaysOnTop(boolean alwaysOnTop)
	{
    System.out.println("setAlwaysOnTop");
	}

	@Override
	public void updateFocusableWindowState()
	{
    System.out.println("updateFocusableWindowState");
	}

	@Override
	public void setModalBlocked(Dialog blocker, boolean blocked)
	{
    System.out.println("setModalBlocked");
	}

	@Override
	public void updateMinimumSize()
	{
    System.out.println("updateMinimumSize");
	}

	@Override
	public void updateIconImages()
	{
    System.out.println("updateIconImages");
	}

	@Override
	public void setOpacity(float opacity)
	{
    System.out.println("setOpacity");
	}

	@Override
	public void setOpaque(boolean isOpaque)
	{
    System.out.println("setOpaque");

	}

	@Override
	public void updateWindow()
	{
    System.out.println("updateWindow");

	}

	@Override
	public void repositionSecurityWarning()
	{
    System.out.println("repositionSecurityWarning");

	}

	@Override
	public Insets getInsets()
	{
    System.out.println("getInsets");
    return new Insets(0, 0, 0, 0);
	}

	@Override
	public void beginValidate()
	{
    System.out.println("beginValidate");

	}

	@Override
	public void endValidate()
	{
    System.out.println("endValidate");

	}

	@Override
	public void beginLayout()
	{
    System.out.println("beginLayout");

	}

	@Override
	public void endLayout()
	{
    System.out.println("endLayout");

	}

	@Override
	public boolean isObscured()
	{
    System.out.println("isObscured");
		return false;
	}

	@Override
	public boolean canDetermineObscurity()
	{
    System.out.println("canDetermineObscurity");
		return false;
	}

	@Override
	public void setVisible(boolean v)
	{
    System.out.println("setVisible");

	}

	@Override
	public void setEnabled(boolean e)
	{
    System.out.println("setEnabled");

	}

	@Override
	public void paint(Graphics g)
	{
    System.out.println("paint");

	}

	@Override
	public void print(Graphics g)
	{
    System.out.println("print");

	}

	@Override
	public void setBounds(int x, int y, int width, int height, int op)
	{
    System.out.format("%d, %d, %d, %d, %d\n", x, y, width, height, op);
    System.out.println("setBounds");

	}

	@Override
	public void handleEvent(AWTEvent e)
	{
    System.out.println("handleEvent");
    System.out.println(e);
	}

	@Override
	public void coalescePaintEvent(PaintEvent e)
	{
    System.out.println("coalescePaintEvent");
    System.out.println(e);
	}

	@Override
	public Point getLocationOnScreen()
	{
    System.out.println("getLocationOnScreen");
    return new Point(50, 200);
	}

	@Override
	public Dimension getPreferredSize()
	{
    System.out.println("getPreferredSize");
		return null;
	}

	@Override
	public Dimension getMinimumSize()
	{
    System.out.println("getMinimumSize");
		return null;
	}

	@Override
	public ColorModel getColorModel()
	{
    System.out.println("getColorModel");
		return null;
	}

	@Override
	public Toolkit getToolkit()
	{
    System.out.println("getToolkit");
    return HackedToolkit.INST;
	}

	@Override
	public Graphics getGraphics()
	{
    System.out.println("getGraphics");
		return null;
	}

	@Override
	public FontMetrics getFontMetrics(Font font)
	{
    System.out.println("getFontMetrics");
		return null;
	}

	@Override
	public void dispose()
	{
    System.out.println("dispose");

	}

	@Override
	public void setForeground(Color c)
	{
    System.out.println("setForeground");

	}

	@Override
	public void setBackground(Color c)
	{
    System.out.println("setBackground");

	}

	@Override
	public void setFont(Font f)
	{
    System.out.println("setFont");

	}

	@Override
	public void updateCursorImmediately()
	{
    System.out.println("updateCursorImmediately");
	}

	@Override
	public boolean requestFocus(Component lightweightChild, boolean temporary,
	        boolean focusedWindowChangeAllowed, long time, Cause cause)
	{
  System.out.println("requestFocus");
		return false;
	}

	@Override
	public boolean isFocusable()
	{
    System.out.println("isFocusable");
		return false;
	}

	@Override
	public Image createImage(ImageProducer producer)
	{
    System.out.println("createImage");
		return null;
	}

	@Override
	public Image createImage(int width, int height)
	{
    System.out.println("createImage");
		return null;
	}

	@Override
	public VolatileImage createVolatileImage(int width, int height)
	{
    System.out.println("createVolatileImage");
		return null;
	}

	@Override
	public boolean prepareImage(Image img, int w, int h, ImageObserver o)
	{
    System.out.println("prepareImage");
		return false;
	}

	@Override
	public int checkImage(Image img, int w, int h, ImageObserver o)
	{
    System.out.println("checkImage");
		return 0;
	}

	@Override
	public GraphicsConfiguration getGraphicsConfiguration()
	{
    System.out.println("getGraphicsConfiguration");
		return null;
	}

	@Override
	public boolean handlesWheelScrolling()
	{
    System.out.println("handlesWheelScrolling");
		return false;
	}

	@Override
	public void createBuffers(int numBuffers, BufferCapabilities caps)
	        throws AWTException
	{
          System.out.println("createBuffers");
	}

	@Override
	public Image getBackBuffer()
	{
    System.out.println("getBackBuffer");
		return null;
	}

	@Override
	public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction)
	{
    System.out.println("flip");

	}

	@Override
	public void destroyBuffers()
	{
    System.out.println("destroyBuffers");

	}

	@Override
	public void reparent(ContainerPeer newContainer)
	{
    System.out.println("reparent");

	}

	@Override
	public boolean isReparentSupported()
	{
    System.out.println("isReparentSupported");
		return false;
	}

	@Override
	public void layout()
	{
    System.out.println("layout");

	}

	@Override
	public void applyShape(Region shape)
	{
    System.out.println("applyShape");

	}

	@Override
	public void setTitle(String title)
	{
    System.out.println("setTitle");

	}

	@Override
	public void setMenuBar(MenuBar mb)
	{
    System.out.println("setMenuBar");

	}

	@Override
	public void setResizable(boolean resizeable)
	{
    System.out.println("setResizable");

	}

	@Override
	public void setState(int state)
	{
    System.out.println("setState");

	}

	@Override
	public int getState()
	{
    System.out.println("getState");
   return 0;
	}

	@Override
	public void setMaximizedBounds(Rectangle bounds)
	{
    System.out.println("setMaximizedBounds");

	}

	@Override
	public void setBoundsPrivate(int x, int y, int width, int height)
	{
    System.out.println("setBoundsPrivate");

	}

	@Override
	public Rectangle getBoundsPrivate()
	{
    System.out.println("getBoundsPrivate");
    return new Rectangle(500, 500);
	}

  @Override
  public boolean requestWindowFocus() {
    System.out.println("requestWindowFocus");
    return false;
  }

  @Override
  public boolean isPaintPending() {
    System.out.println("isPaintPending");
    return false;
  }

  @Override
  public void restack() {
  System.out.println("restack");
  }

  @Override
  public boolean isRestackSupported() {
    System.out.println("isRestackSupported");
    return false;
  }

  @Override
  public Insets insets() {
    System.out.println("insets");
    return getInsets();
  }

  @Override
  public void repaint(long tm, int x, int y, int width, int height) {
  System.out.println("repaint");
  }

  @Override
  public Rectangle getBounds() {
    System.out.println("getBounds");
    return getBoundsPrivate();
  }

  @Override
  public Dimension preferredSize() {
    System.out.println("preferredSize");
    return null;
  }

  @Override
  public Dimension minimumSize() {
    System.out.println("minimumSize");
    return null;
  }

  @Override
  public void show() {
  System.out.println("show");
  }

  @Override
  public void hide() {
  System.out.println("hide");
  }

  @Override
  public void enable() {
  System.out.println("enable");
  }

  @Override
  public void disable() {
  System.out.println("disable");
  }

  @Override
  public void reshape(int x, int y, int width, int height) {
  System.out.println("reshape");
  }
}

