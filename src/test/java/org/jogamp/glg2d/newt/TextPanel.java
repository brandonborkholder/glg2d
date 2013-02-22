package org.jogamp.glg2d.newt;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;

/**
 * This demonstration two issues:
 * 
 * 1) Text fields do not show cursors and cannot be modified (missing text).
 * 
 * 2) Scrolling the scroll bar fails if you do it really fast. Not sure why.
 * 
 * @author Dan Avila
 * 
 */
public class TextPanel extends GLG2DWindowTest
{
	public static void main(String[] args)
	{
		new TextPanel();
	}

	@Override
	protected JComponent getContentPane()
	{
		JPanel textPanel = new JPanel(new GridLayout(2, 2));

		String text = "";

		for (int i = 0; i < 100; i++)
		{
			text += "Text area.\n";
		}

		JScrollPane scroller = new JScrollPane(new JTextArea(text));
    scroller.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
    textPanel.add(scroller);
		JTextField f = new JTextField("Text field.");
    f.requestFocusInWindow();
    f.setCaretPosition(3);
    textPanel.add(f);
		textPanel.add(new JFormattedTextField("Formatted text field."));
		textPanel.add(new JPasswordField("Password"));

		return textPanel;
	}

}
