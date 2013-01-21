package org.jogamp.glg2d.newt;

import java.awt.AWTEvent;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.media.nativewindow.NativeWindow;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.swing.JComponent;
import javax.swing.JRootPane;

import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.Window;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
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
	private static final Class<?>[] INTERFACES = new Class<?>[]
	{ MouseListener.class, KeyListener.class, WindowListener.class };

	private class EventHandler implements InvocationHandler
	{
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
		        throws Throwable
		{
			EventQueue eventQueue = Toolkit.getDefaultToolkit()
			        .getSystemEventQueue();

			if (args.length > 0)
			{
				AWTEvent event = null;

				if (args[0] instanceof MouseEvent)
				{
					try
					{
						event = NewtAWTEventFactory.convertMouseEvent(
						        container.getContentPane(),
						        (MouseEvent) args[0]);
					}
					catch (RuntimeException e)
					{
						e.printStackTrace();
					}

					System.err.println(event);

				}
				else if (args[0] instanceof KeyEvent)
				{
					event = NewtAWTEventFactory
					        .convertKeyEvent((KeyEvent) args[0]);
				}
				else if (args[0] instanceof WindowEvent)
				{
					event = NewtAWTEventFactory
					        .convertWindowEvent((WindowEvent) args[0]);
				}

				if (event != null)
				{
					eventQueue.postEvent(event);
				}
			}

			return null;
		}
	}

	/**
	 * Raises the visibility of some methods for our internal use only.
	 * 
	 * @author Dan Avila
	 * 
	 */
	private static class CustomRootPane extends JRootPane
	{
		private static final long serialVersionUID = 3892943055189213178L;

		@Override
		public void processEvent(AWTEvent e)
		{
			super.processEvent(e);
		}
	}

	private GLG2DNewEventListener listener;

	private CustomRootPane container = new CustomRootPane();

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

		Object handler = Proxy.newProxyInstance(
		        ClassLoader.getSystemClassLoader(), INTERFACES,
		        new EventHandler());

		addKeyListener(KeyListener.class.cast(handler));
		addMouseListener(MouseListener.class.cast(handler));
		addWindowListener(WindowListener.class.cast(handler));

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

			this.removeGLEventListener(listener);
			this.listener = new GLG2DNewEventListener(component);

			this.addGLEventListener(listener);
		}
	}

	/**
	 * The component returned from here can be used to draw things.
	 * 
	 * @return the content pane, or null if it hasn't been
	 *         {@link #setContentPane(JComponent) set}
	 */
	public Container getContentPane()
	{
		return container.getContentPane();
	}

	public static GLG2DWindow create(GLCapabilitiesImmutable caps)
	{
		Window window = NewtFactory.createWindow(caps);

		return new GLG2DWindow(window);
	}

	public static GLG2DWindow create(NativeWindow parentNativeWindow,
	        GLCapabilitiesImmutable caps)
	{
		Window window = NewtFactory.createWindow(parentNativeWindow, caps);

		return new GLG2DWindow(window);
	}

	public static GLG2DWindow create(Screen screen, GLCapabilitiesImmutable caps)
	{
		Window window = NewtFactory.createWindow(screen, caps);

		return new GLG2DWindow(window);
	}

	public static GLG2DWindow create(Window window, GLCapabilitiesImmutable caps)
	{
		Window base = NewtFactory.createWindow(window, caps);

		return new GLG2DWindow(base);
	}
}
