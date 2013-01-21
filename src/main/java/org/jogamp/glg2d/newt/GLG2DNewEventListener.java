package org.jogamp.glg2d.newt;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.JComponent;

import org.jogamp.glg2d.GLG2DSimpleEventListener;

/**
 * Helps wrap the {@code GLGraphics2D} object within the JOGL framework and
 * paints the component fully for each display.
 */
public class GLG2DNewEventListener extends GLG2DSimpleEventListener
{
	public GLG2DNewEventListener(JComponent baseComponent)
	{
		super(baseComponent);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This method also resizes the base component, assuming it has been added.
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
	        int height)
	{
		if (baseComponent != null)
		{
			baseComponent.setSize(width, height);
			baseComponent.doLayout();
		}

		super.reshape(drawable, x, y, width, height);
	}
}
