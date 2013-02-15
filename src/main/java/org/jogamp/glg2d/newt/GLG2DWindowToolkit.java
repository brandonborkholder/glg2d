package org.jogamp.glg2d.newt;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DesktopPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.LightweightPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.WindowPeer;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import sun.awt.KeyboardFocusManagerPeerProvider;

/**
 * -Dawt.toolkit=org.jogamp.glg2d.newt.GLG2DWindowToolkit
 * 
 * @author Naval Undersea Warfare Center, Newport RI
 * 
 */
public class GLG2DWindowToolkit extends Toolkit implements
        KeyboardFocusManagerPeerProvider
{
	public static final Toolkit INST = new GLG2DWindowToolkit();

	private EventQueue event = new EventQueue();

	@Override
	protected LightweightPeer createComponent(Component target)
	{
		// LightweightPeer peer = new GLG2DLightweightPeer(target);
		//
		// return peer;

		return super.createComponent(target);
	}

	@Override
	protected DesktopPeer createDesktopPeer(Desktop target)
	        throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ButtonPeer createButton(Button target) throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TextFieldPeer createTextField(TextField target)
	        throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected LabelPeer createLabel(Label target) throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ListPeer createList(List target) throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CheckboxPeer createCheckbox(Checkbox target)
	        throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ScrollbarPeer createScrollbar(Scrollbar target)
	        throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ScrollPanePeer createScrollPane(ScrollPane target)
	        throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TextAreaPeer createTextArea(TextArea target)
	        throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ChoicePeer createChoice(Choice target) throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FramePeer createFrame(Frame target) throws HeadlessException
	{
		FramePeer peer = new GLG2DWindowPeer(target);

		return peer;
	}

	@Override
	protected CanvasPeer createCanvas(Canvas target)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PanelPeer createPanel(Panel target)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected WindowPeer createWindow(Window target) throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected DialogPeer createDialog(Dialog target) throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected MenuBarPeer createMenuBar(MenuBar target)
	        throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected MenuPeer createMenu(Menu target) throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PopupMenuPeer createPopupMenu(PopupMenu target)
	        throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected MenuItemPeer createMenuItem(MenuItem target)
	        throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FileDialogPeer createFileDialog(FileDialog target)
	        throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CheckboxMenuItemPeer createCheckboxMenuItem(
	        CheckboxMenuItem target) throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	protected FontPeer getFontPeer(String name, int style)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension getScreenSize() throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getScreenResolution() throws HeadlessException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ColorModel getColorModel() throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public String[] getFontList()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public FontMetrics getFontMetrics(Font font)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sync()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Image getImage(String filename)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getImage(URL url)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image createImage(String filename)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image createImage(URL url)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean prepareImage(Image image, int width, int height,
	        ImageObserver observer)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int checkImage(Image image, int width, int height,
	        ImageObserver observer)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Image createImage(ImageProducer producer)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image createImage(byte[] imagedata, int imageoffset, int imagelength)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PrintJob getPrintJob(Frame frame, String jobtitle, Properties props)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void beep()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Clipboard getSystemClipboard() throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EventQueue getSystemEventQueueImpl()
	{
		return event;
	}

	@Override
	public DragSourceContextPeer createDragSourceContextPeer(
	        DragGestureEvent dge) throws InvalidDnDOperationException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isModalityTypeSupported(ModalityType modalityType)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isModalExclusionTypeSupported(
	        ModalExclusionType modalExclusionType)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<TextAttribute, ?> mapInputMethodHighlight(
	        InputMethodHighlight highlight) throws HeadlessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KeyboardFocusManagerPeer createKeyboardFocusManagerPeer(
	        KeyboardFocusManager arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
