package org.jogamp.glg2d.newt;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseWheelEvent;

import jogamp.newt.awt.event.AWTNewtEventFactory;

import com.jogamp.common.util.IntIntHashMap;
import com.jogamp.common.util.IntIntHashMap.Entry;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.WindowEvent;

/**
 * This class extends the {@link AWTNewtEventFactory} by providing inverse
 * operations.
 * 
 * @author Dan Avila
 * @see AWTNewtEventFactory
 */
public class NewtAWTEventFactory extends AWTNewtEventFactory
{
	/**
	 * This is an inverse map {@link AWTNewtEventFactory#eventTypeAWT2NEWT}
	 */
	private final static IntIntHashMap EVENT_TYPE_NEWT_2_AWT;
	private final static IntIntHashMap BUTTON_MASKS_NEWT_2_AWT;

	private static final int KEY_NOT_FOUND = 0xFFFFFFFF;

	static
	{
		EVENT_TYPE_NEWT_2_AWT = new IntIntHashMap();
		EVENT_TYPE_NEWT_2_AWT.setKeyNotFoundValue(KEY_NOT_FOUND);

		for (Entry entry : AWTNewtEventFactory.eventTypeAWT2NEWT)
		{
			EVENT_TYPE_NEWT_2_AWT.put(entry.getValue(), entry.getKey());
		}

		BUTTON_MASKS_NEWT_2_AWT = new IntIntHashMap();
		BUTTON_MASKS_NEWT_2_AWT.setKeyNotFoundValue(KEY_NOT_FOUND);
		BUTTON_MASKS_NEWT_2_AWT.put(0, 0);

		addButtonMask(MouseEvent.BUTTON1);
		addButtonMask(MouseEvent.BUTTON2);
		addButtonMask(MouseEvent.BUTTON3);
		addButtonMask(MouseEvent.BUTTON4);
		addButtonMask(MouseEvent.BUTTON5);
		addButtonMask(MouseEvent.BUTTON6);
		addButtonMask(MouseEvent.BUTTON7);
		addButtonMask(MouseEvent.BUTTON8);
		addButtonMask(MouseEvent.BUTTON9);
	}

	private static final void addButtonMask(int newtMask)
	{
		BUTTON_MASKS_NEWT_2_AWT.put(getAWTButtonDownMask(newtMask), newtMask);
	}

	/**
	 * Creates an AWT Mouse event from the provided Newt mouse event.
	 * 
	 * @param root
	 *            - the top-level container.
	 * @param event
	 *            - the Newt-generated mouse event.
	 * @return the awt mouse event.
	 */
	public static AWTEvent convertMouseEvent(Container root, MouseEvent event)
	{
		int id = EVENT_TYPE_NEWT_2_AWT.get(event.getEventType());

		if (id == KEY_NOT_FOUND)
		{
			return null;
		}

		long when = event.getWhen();
		int modifiers = event.getModifiers();
		int x, xAbs, y, yAbs;
		x = xAbs = event.getX();
		y = yAbs = event.getY();
		Component source = root.getComponentAt(x, y);
		source = source == null ? root : source;
		int clickCount = event.getClickCount();
		boolean popupTrigger = false;

		switch (event.getEventType())
		{

		case MouseEvent.EVENT_MOUSE_WHEEL_MOVED:

			int scrollType = MouseWheelEvent.WHEEL_UNIT_SCROLL;
			int scrollAmount = 1;

			// see NewtAWTFactory;
			int wheelRotation = -1 * event.getWheelRotation();

			return new java.awt.event.MouseWheelEvent(source, id, when,
			        modifiers, x, y, xAbs, yAbs, clickCount, popupTrigger,
			        scrollType, scrollAmount, wheelRotation);

		default:

			int button = event.getButton();

			return new java.awt.event.MouseEvent(source, id, when, modifiers,
			        x, y, xAbs, yAbs, clickCount, popupTrigger, button);
		}
	}

	public static AWTEvent convertKeyEvent(KeyEvent keyEvent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static AWTEvent convertWindowEvent(WindowEvent object)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
