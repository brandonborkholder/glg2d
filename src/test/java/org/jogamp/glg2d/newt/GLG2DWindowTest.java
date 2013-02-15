package org.jogamp.glg2d.newt;

import java.util.concurrent.Executors;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;

import org.jogamp.glg2d.GLG2DPanel;

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
		// FIXME: Nimbus causes a NullPointerException

		// for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
		// {
		// if ("nimbus".equals(info.getName().toLowerCase()))
		// {
		// try
		// {
		// UIManager.setLookAndFeel(info.getClassName());
		// }
		// catch (ClassNotFoundException | InstantiationException
		// | IllegalAccessException
		// | UnsupportedLookAndFeelException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// break;
		// }
		// }

		// createAndShowGLG2DPanel();

		createAndShowNewtPanel();
	}

	private static void createAndShowNewtPanel()
	{
		GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
		caps.setDoubleBuffered(true);
		caps.setNumSamples(4);
		caps.setSampleBuffers(true);

		GLG2DWindow window = GLG2DWindow.create(caps);

		ContentPane pane = new ContentPane();

		window.setContentPane(pane);
		window.setSize(600, 600);
		window.setVisibler(true);

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

	private static void createAndShowGLG2DPanel()
	{
		JFrame window = new JFrame();

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GLG2DPanel panel = new GLG2DPanel();
		panel.setDrawableComponent(new ContentPane());

		window.setContentPane(panel);
		window.setSize(600, 600);

		window.setVisible(true);
	}
}
