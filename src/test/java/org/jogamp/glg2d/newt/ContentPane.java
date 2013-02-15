package org.jogamp.glg2d.newt;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class ContentPane extends JPanel
{
	private static final long serialVersionUID = 7824314575532663002L;

	private CardLayout cardLayout = new CardLayout();

	public ContentPane()
	{
		setLayout(new GridLayout(2, 2));
		setBackground(Color.GREEN);

		add(new JLabel("This should be an accelerated canvas."));

		JPanel swapPanel = createSwapPanel();
		add(swapPanel);

		add(new JSlider(JSlider.HORIZONTAL));

		JTabbedPane pane = new JTabbedPane();
		add(pane);

		JProgressBar bar = new JProgressBar();
		bar.setIndeterminate(true);
		pane.addTab("Progress Bar", bar);

		int gridSize = 6;
		JPanel buttonPanel = new JPanel(new GridLayout(gridSize, gridSize));

		for (int i = 0; i < gridSize * gridSize; i++)
		{
			AbstractButton button = createRandomButtonType();
			button.addActionListener(new PrintIfVisibleAction());

			buttonPanel.add(button);
		}

		pane.addTab("Button Panel", buttonPanel);

		JPanel textPanel = new JPanel(new GridLayout(2, 2));

		pane.addTab("Text Comps", textPanel);
		textPanel
		        .add(new JScrollPane(
		                new JTextArea(
		                        "Text area.\nText area.\nText area.\nText area.\nText area.\nText area.\nText area.\nText area.\nText area.\nText area.\nText area.\nText area.\nText area.\nText area.\nText area.\nText area.\nText area.\n")));
		textPanel.add(new JTextField("Text field."));
		textPanel.add(new JFormattedTextField("Formatted text field."));
		textPanel.add(new JPasswordField("Password"));

		JPanel swapTab = new JPanel();

		swapTab.add(createColorButton("RED", swapPanel));
		swapTab.add(createColorButton("BLUE", swapPanel));

		pane.addTab("Color Swap", swapTab);
	}

	private JButton createColorButton(final String name, final Container parent)
	{
		JButton button = new JButton(name);

		button.setText(name);

		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				cardLayout.show(parent, name);

				for (Component comp : parent.getComponents())
				{
					System.err.println(comp.getName() + " showing="
					        + comp.isShowing());
				}
			}
		});

		return button;
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

	private JPanel createSwapPanel()
	{
		JPanel panel = new JPanel(cardLayout);

		JPanel background = new JPanel();
		background.setName("RED");
		background.setBackground(Color.RED);
		panel.add(background, "RED");

		background = new JPanel();
		background.setName("BLUE");
		background.setBackground(Color.BLUE);
		panel.add(background, "BLUE");

		return panel;
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
