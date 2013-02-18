package org.jogamp.glg2d.newt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;

import org.jogamp.glg2d.GLG2DCanvas;

import com.jogamp.newt.opengl.GLWindow;

class GLG2DFrame extends JFrame
{
	private static final long serialVersionUID = 8999015711459748410L;

	private GLWindow window;

	public GLG2DFrame(GLWindow window)
	{
		this.window = window;
	}

	public GLWindow getWindow()
	{
		return window;
	}

	@Override
	public void addNotify()
	{
		verifyHierarchy(this);

		super.addNotify();
	}

	/**
	 * 
	 * @param comp
	 * @see GLG2DCanvas#verifyHierarchy(Component comp)
	 */
	protected void verifyHierarchy(Component comp)
	{
		if (comp instanceof JViewport)
		{
			((JViewport) comp).setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		}

		if (comp instanceof Container)
		{
			Container cont = (Container) comp;
			for (int i = 0; i < cont.getComponentCount(); i++)
			{
				verifyHierarchy(cont.getComponent(i));
			}
		}
	}

	@Override
	public Graphics getGraphics()
	{
		// TODO Auto-generated method stub
		return super.getGraphics();
	}
}
