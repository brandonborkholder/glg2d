package org.jogamp.glg2d.newt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

public class TestButton extends JButton
{
	private static final long serialVersionUID = 4351957725276352018L;

	public TestButton(String string)
	{
		super(string);

		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// System.err.println(getText() + " was clicked!");
			}
		});
	}

	@Override
	protected void processMouseEvent(MouseEvent e)
	{
		// System.err.println(e);

		super.processMouseEvent(e);
	}

	@Override
	public String toString()
	{
		return getText() + " " + super.toString();
	}
}