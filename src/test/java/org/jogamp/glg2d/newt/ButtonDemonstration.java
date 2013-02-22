package org.jogamp.glg2d.newt;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

/**
 * This test demonstrates an weird draw issue where buttons maintain their
 * "pressed" looked when you click on them and then drag the mouse away.
 * 
 * @author Dan Avila
 * 
 */
public class ButtonDemonstration extends GLG2DWindowTest
{
	public static void main(String[] args)
	{
		new ButtonDemonstration();
	}

	@Override
	protected JComponent getContentPane()
	{
		int gridSize = 6;
		JPanel buttonPanel = new JPanel(new GridLayout(gridSize, gridSize));

		for (int i = 0; i < gridSize * gridSize; i++)
		{
			AbstractButton button = createRandomButtonType();
			button.addActionListener(new PrintIfVisibleAction());
      MouseAdapter l = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
//          System.out.println(e);
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
//          System.out.println(e);
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
//          System.out.println(e);
        }
        
        @Override
        public void mouseMoved(MouseEvent e) {
//          System.out.println(e);
        }
      };
      button.addMouseListener(l);
      button.addMouseMotionListener(l);

			buttonPanel.add(button);
		}

		return buttonPanel;
	}

	private AbstractButton createRandomButtonType()
	{
		int value = (int) (Math.random() * 4);

		switch (value)
		{
		case 0:
			return new JToggleButton("T");
		case 1:
			return new JCheckBox("C");
		case 2:
			return new JRadioButton("R");
		default:
			return new JButton("B");
		}
	}

	private static class PrintIfVisibleAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			AbstractButton button = (AbstractButton) e.getSource();

			if (button.isShowing())
			{
				String name = button.getClass().getSimpleName();

				System.err.println(name + " clicked!");
			}
		}
	}
}
