package org.jogamp.glg2d.newt;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class AWTWindowTest
{
	public static void main(String args[])
	{
		JFrame window = new JFrame();

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		panel.setBackground(Color.GREEN);

		panel.add(new TestButton("TOP"));
		panel.add(new TestButton("BOTTOM"));

		window.setContentPane(panel);
		window.setSize(300, 300);
		window.setVisible(true);

		EventQueue eventQueue = Toolkit.getDefaultToolkit()
		        .getSystemEventQueue();

		eventQueue.push(new EventQueue()
		{
			@Override
			protected void dispatchEvent(AWTEvent event)
			{
				System.err.println(event);

				super.dispatchEvent(event);
			}
		});
		//
		// frame.addMouseWheelListener(new MouseWheelListener()
		// {
		//
		// @Override
		// public void mouseWheelMoved(MouseWheelEvent e)
		// {
		// // System.err.println(e);
		// }
		// });
		//
		// frame.addMouseMotionListener(new MouseMotionListener()
		// {
		// @Override
		// public void mouseMoved(MouseEvent e)
		// {
		// // System.err.println(e);
		// }
		//
		// @Override
		// public void mouseDragged(MouseEvent e)
		// {
		// // System.err.println(e);
		// }
		// });
		//
		// frame.addMouseListener(new MouseListener()
		// {
		// @Override
		// public void mouseReleased(MouseEvent e)
		// {
		// // System.err.println(e);
		// }
		//
		// @Override
		// public void mousePressed(MouseEvent e)
		// {
		// // System.err.println(e);
		// }
		//
		// @Override
		// public void mouseExited(MouseEvent e)
		// {
		// // System.err.println(e);
		// }
		//
		// @Override
		// public void mouseEntered(MouseEvent e)
		// {
		// // System.err.println(e);
		// }
		//
		// @Override
		// public void mouseClicked(MouseEvent e)
		// {
		// // System.err.println(e);
		// }
		// });

		window.setEnabled(true);
		window.setSize(200, 200);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
