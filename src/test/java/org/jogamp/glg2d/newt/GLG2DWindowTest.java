package org.jogamp.glg2d.newt;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.concurrent.Executors;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.JPanel;
import javax.swing.JSlider;

import com.jogamp.opengl.util.Animator;

/**
 * A simple test to demonstrate a very basic Swing Heirarchy rendered in an
 * accelerated window.
 * 
 * @author Dan Avila
 * 
 */
public class GLG2DWindowTest
{
	public static void main(String[] args)
	{
		GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
		caps.setDoubleBuffered(true);
		caps.setNumSamples(4);
		caps.setSampleBuffers(true);

		GLG2DWindow window = GLG2DWindow.create(caps);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		panel.setBackground(Color.GREEN);

		// JPanel panel2 = new JPanel();
		// panel2.setOpaque(true);
		// panel2.setBackground(Color.RED);

		panel.add(new TestButton("TOP"));
		// panel.add(panel2);
		panel.add(new TestButton("BOTTOM"));
    
		panel.add(new JSlider(JSlider.HORIZONTAL));

		window.setContentPane(panel);
		window.setSize(300, 300);
		// window.setFullscreen(true);
		window.setVisible(true);

		final Animator animator = new Animator();
		animator.setRunAsFastAsPossible(true);
		animator.add(window);

		Executors.newSingleThreadExecutor().execute(new Runnable()
		{
			@Override
			public void run()
			{
				animator.start();
			}
		});
	}
}
