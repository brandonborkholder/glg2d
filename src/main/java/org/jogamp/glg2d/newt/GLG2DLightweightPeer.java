package org.jogamp.glg2d.newt;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.awt.peer.LightweightPeer;

import sun.awt.NullComponentPeer;
import sun.awt.CausedFocusEvent.Cause;
import sun.java2d.pipe.Region;

/**
 * This is a modified version of the {@link NullComponentPeer} provided by sun.
 * This class implements basic peer functionality.
 * 
 * @author Naval Undersea Warfare Center, Newport RI
 * 
 */
public class GLG2DLightweightPeer implements LightweightPeer
{
	private Component target;

	public GLG2DLightweightPeer(Component target)
	{
		this.target = target;
	}

	private void log()
	{
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		String name = target.getClass().getSimpleName();

		System.err.println(name + ": " + trace[2]);
	}

	@Override
	public boolean isObscured()
	{
		log();

		return false;
	}

	@Override
	public boolean canDetermineObscurity()
	{
		log();
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setVisible(boolean v)
	{
		log();
		// TODO Auto-generated method stub
	}

	@Override
	public void setEnabled(boolean e)
	{
		log();
		// TODO Auto-generated method stub

	}

	@Override
	public void paint(Graphics g)
	{
		log();
		// TODO Auto-generated method stub

	}

	@Override
	public void print(Graphics g)
	{
		log();
		// TODO Auto-generated method stub

	}

	@Override
	public void setBounds(int x, int y, int width, int height, int op)
	{
		log();
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(AWTEvent e)
	{
		// log();
		// TODO Auto-generated method stub
	}

	@Override
	public void coalescePaintEvent(PaintEvent e)
	{
		log();
		// TODO Auto-generated method stub

	}

	@Override
	public Point getLocationOnScreen()
	{
		log();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension getPreferredSize()
	{
		log();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension getMinimumSize()
	{
		log();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ColorModel getColorModel()
	{
		log();
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
		log();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FontMetrics getFontMetrics(Font font)
	{
		log();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose()
	{
		log();
		// TODO Auto-generated method stub

	}

	@Override
	public void setForeground(Color c)
	{
		log();
		// TODO Auto-generated method stub

	}

	@Override
	public void setBackground(Color c)
	{
		log();
		// TODO Auto-generated method stub

	}

	@Override
	public void setFont(Font f)
	{
		log();
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCursorImmediately()
	{
		log();
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requestFocus(Component lightweightChild, boolean temporary,
	        boolean focusedWindowChangeAllowed, long time, Cause cause)
	{
		// TODO Auto-generated method stub
		log();
		return false;
	}

	@Override
	public boolean isFocusable()
	{
		// TODO Auto-generated method stub
		log();
		return false;
	}

	@Override
	public Image createImage(ImageProducer producer)
	{
		// TODO Auto-generated method stub
		log();
		return null;
	}

	@Override
	public Image createImage(int width, int height)
	{
		// TODO Auto-generated method stub
		log();
		return null;
	}

	@Override
	public VolatileImage createVolatileImage(int width, int height)
	{
		// TODO Auto-generated method stub
		log();
		return null;
	}

	@Override
	public boolean prepareImage(Image img, int w, int h, ImageObserver o)
	{
		// TODO Auto-generated method stub
		log();
		return false;
	}

	@Override
	public int checkImage(Image img, int w, int h, ImageObserver o)
	{
		// TODO Auto-generated method stub
		log();
		return 0;
	}

	@Override
	public GraphicsConfiguration getGraphicsConfiguration()
	{
		// TODO Auto-generated method stub
		log();
		return null;
	}

	@Override
	public boolean handlesWheelScrolling()
	{
		// TODO Auto-generated method stub
		log();
		return false;
	}

	@Override
	public void createBuffers(int numBuffers, BufferCapabilities caps)
	        throws AWTException
	{
		// TODO Auto-generated method stub
		log();

	}

	@Override
	public Image getBackBuffer()
	{
		// TODO Auto-generated method stub
		log();
		return null;
	}

	@Override
	public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction)
	{
		// TODO Auto-generated method stub
		log();

	}

	@Override
	public void destroyBuffers()
	{
		// TODO Auto-generated method stub
		log();

	}

	@Override
	public void reparent(ContainerPeer newContainer)
	{
		// TODO Auto-generated method stub
		log();

	}

	@Override
	public boolean isReparentSupported()
	{
		// TODO Auto-generated method stub
		log();
		return false;
	}

	@Override
	public void layout()
	{
		// TODO Auto-generated method stub
		log();

	}

	@Override
	public void applyShape(Region shape)
	{
		// TODO Auto-generated method stub
		log();

	}

	@Override
	public void setZOrder(ComponentPeer above)
	{
		// TODO Auto-generated method stub
		log();

	}

	@Override
	public boolean updateGraphicsData(GraphicsConfiguration gc)
	{
		log();
		// TODO Auto-generated method stub
		return false;
	}
}
