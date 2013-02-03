package org.jogamp.glg2d.newt;

import javax.media.nativewindow.NativeWindow;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.swing.JComponent;
import javax.swing.JRootPane;

import org.jogamp.glg2d.GLG2DHeadlessListener;
import org.jogamp.glg2d.GLG2DSimpleEventListener;
import org.jogamp.glg2d.event.NewtMouseEventTranslator;

import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.Window;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;

/**
 * An extension to the GLWindow class that can handle rendering
 * {@link #setRootPane(JComponent) Swing components}.
 * 
 * Check the <i>See Also</i> section for additional ways to create canvases.
 * 
 * @author Dan Avila
 * 
 * @see #create(GLCapabilitiesImmutable)
 * @see #create(NativeWindow, GLCapabilitiesImmutable)
 * @see #create(Screen, GLCapabilitiesImmutable)
 * @see #create(Window)
 */
public class GLG2DWindow extends GLWindow
{
	private GLG2DSimpleEventListener painterListener;
	private GLG2DHeadlessListener reshapeListener;
  private NewtMouseEventTranslator evtListener;

  private JRootPane container = new JRootPane();

	/**
	 * Creates a new GLG2DWindow.
	 * 
	 * Registers an handler that will forward mouse, keyboard, and window events
	 * to the {@link #getContentPane() content pane}.
	 * 
	 * @param window
	 *            - the native window implementation.
	 */
	protected GLG2DWindow(Window window)
	{
		super(window);

		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowDestroyed(WindowEvent e)
			{
				System.exit(0);
			}
		});
	}

	/**
	 * The content pane.
	 * 
	 * @param component
	 */
	public void setContentPane(JComponent component)
	{
		if (component != null)
		{
			this.container.setContentPane(component);

			this.removeGLEventListener(painterListener);
			this.removeGLEventListener(reshapeListener);
      painterListener = new GLG2DSimpleEventListener(component);
      reshapeListener = new GLG2DHeadlessListener(component);
      addGLEventListener(painterListener);
      addGLEventListener(reshapeListener);
      
			this.removeMouseListener(evtListener);
			this.evtListener = new NewtMouseEventTranslator(component);
      addMouseListener(evtListener);
		}
	}

	public static GLG2DWindow create(GLCapabilitiesImmutable caps)
	{
		Window window = NewtFactory.createWindow(caps);

		return new GLG2DWindow(window);
	}
}
