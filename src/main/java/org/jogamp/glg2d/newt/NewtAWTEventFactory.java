package org.jogamp.glg2d.newt;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

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

	private static final int KEY_NOT_FOUND = 0xFFFFFFFF;

	static
	{
		EVENT_TYPE_NEWT_2_AWT = new IntIntHashMap();
		EVENT_TYPE_NEWT_2_AWT.setKeyNotFoundValue(KEY_NOT_FOUND);

		for (Entry entry : AWTNewtEventFactory.eventTypeAWT2NEWT)
		{
			EVENT_TYPE_NEWT_2_AWT.put(entry.getValue(), entry.getKey());
		}
	}

  private static int newtModifiers2Awt(int newtMods) {
    int awtMods = 0;
    if ((newtMods & com.jogamp.newt.event.InputEvent.SHIFT_MASK) != 0)     awtMods |= java.awt.event.InputEvent.SHIFT_MASK;
    if ((newtMods & com.jogamp.newt.event.InputEvent.CTRL_MASK) != 0)      awtMods |= java.awt.event.InputEvent.CTRL_MASK;
    if ((newtMods & com.jogamp.newt.event.InputEvent.META_MASK) != 0)      awtMods |= java.awt.event.InputEvent.META_MASK;
    if ((newtMods & com.jogamp.newt.event.InputEvent.ALT_MASK) != 0)       awtMods |= java.awt.event.InputEvent.ALT_MASK;
    if ((newtMods & com.jogamp.newt.event.InputEvent.ALT_GRAPH_MASK) != 0) awtMods |= java.awt.event.InputEvent.ALT_GRAPH_MASK;
    return awtMods;
  }
  
  private static int newtButton2Awt(int newtButton) {
    switch (newtButton) {
    case com.jogamp.newt.event.MouseEvent.BUTTON1: return java.awt.event.MouseEvent.BUTTON1;
    case com.jogamp.newt.event.MouseEvent.BUTTON2: return java.awt.event.MouseEvent.BUTTON2;
    case com.jogamp.newt.event.MouseEvent.BUTTON3: return java.awt.event.MouseEvent.BUTTON3;
    }
    return 0;
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
    Point p = new Point(event.getX(), event.getY());
//    Point absP = new Point(p.x + root.getLocationOnScreen().x, p.y + root.getLocationOnScreen().y);
    Point absP = p;
		Component source = root.getComponentAt(p);
		source = source == null ? root : source;
    p = SwingUtilities.convertPoint(root, p, source);
		int clickCount = event.getClickCount();
		boolean popupTrigger = false;
    
		modifiers = newtModifiers2Awt(modifiers);

		switch (event.getEventType())
		{

		case MouseEvent.EVENT_MOUSE_WHEEL_MOVED:

			int scrollType = MouseWheelEvent.WHEEL_UNIT_SCROLL;
			int scrollAmount = 1;

			// see NewtAWTFactory;
			int wheelRotation = -1 * event.getWheelRotation();

			return new java.awt.event.MouseWheelEvent(source, id, when,
			        modifiers, p.x, p.y, absP.x, absP.y, clickCount, popupTrigger,
			        scrollType, scrollAmount, wheelRotation);

		default:

			int button = event.getButton();
      button = newtButton2Awt(button);

			return new java.awt.event.MouseEvent(source, id, when, modifiers,
			        p.x, p.y, absP.x, absP.y, clickCount, popupTrigger, button);
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
