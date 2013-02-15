package org.jogamp.glg2d.newt;

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

public class GLG2DWindowPeer implements FramePeer
{
	private GLG2DFrame frame;

	public GLG2DWindowPeer(Frame target)
	{
		if (target instanceof GLG2DFrame)
		{
			frame = (GLG2DFrame) target;

			GLWindow window = frame.getWindow();
		}
		else
		{
			throw new IllegalArgumentException(
			        "Frame must be a GLG2DFrame when using this toolkit.");
		}
	}

	@Override
	public void toFront()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void toBack()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setAlwaysOnTop(boolean alwaysOnTop)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFocusableWindowState()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setModalBlocked(Dialog blocker, boolean blocked)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateMinimumSize()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateIconImages()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setOpacity(float opacity)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setOpaque(boolean isOpaque)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateWindow()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void repositionSecurityWarning()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Insets getInsets()
	{
		GLWindow window = frame.getWindow();

		InsetsImmutable glinsets = window.getInsets();

		Insets insets = new Insets(glinsets.getTopHeight(),
		        glinsets.getLeftWidth(), glinsets.getBottomHeight(),
		        glinsets.getRightWidth());

		return insets;
	}

	@Override
	public void beginValidate()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void endValidate()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void beginLayout()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void endLayout()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isObscured()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDetermineObscurity()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setVisible(boolean v)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setEnabled(boolean e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void paint(Graphics g)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void print(Graphics g)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setBounds(int x, int y, int width, int height, int op)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(AWTEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void coalescePaintEvent(PaintEvent e)
	{
		// TODO Auto-generated method stub

	}

	private Point awtTempPoint = new Point();

	@Override
	public Point getLocationOnScreen()
	{
		GLWindow window = frame.getWindow();

		awtTempPoint.x = window.getX();
		awtTempPoint.y = window.getY();

		return awtTempPoint;
	}

	@Override
	public Dimension getPreferredSize()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension getMinimumSize()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ColorModel getColorModel()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Toolkit getToolkit()
	{
		return Toolkit.getDefaultToolkit();
	}

	@Override
	public Graphics getGraphics()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FontMetrics getFontMetrics(Font font)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setForeground(Color c)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setBackground(Color c)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setFont(Font f)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCursorImmediately()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requestFocus(Component lightweightChild, boolean temporary,
	        boolean focusedWindowChangeAllowed, long time, Cause cause)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFocusable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Image createImage(ImageProducer producer)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image createImage(int width, int height)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VolatileImage createVolatileImage(int width, int height)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean prepareImage(Image img, int w, int h, ImageObserver o)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int checkImage(Image img, int w, int h, ImageObserver o)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public GraphicsConfiguration getGraphicsConfiguration()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean handlesWheelScrolling()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createBuffers(int numBuffers, BufferCapabilities caps)
	        throws AWTException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Image getBackBuffer()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void destroyBuffers()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void reparent(ContainerPeer newContainer)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isReparentSupported()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void layout()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void applyShape(Region shape)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setZOrder(ComponentPeer above)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean updateGraphicsData(GraphicsConfiguration gc)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTitle(String title)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setMenuBar(MenuBar mb)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setResizable(boolean resizeable)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setState(int state)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getState()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaximizedBounds(Rectangle bounds)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setBoundsPrivate(int x, int y, int width, int height)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Rectangle getBoundsPrivate()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
