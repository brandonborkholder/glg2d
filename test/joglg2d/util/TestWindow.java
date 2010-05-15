package joglg2d.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.Timer;

import joglg2d.JOGLPanel;

import org.junit.Assert;

/**
 * @author borkholder
 * @created Feb 6, 2010
 *
 */
@SuppressWarnings("serial")
public class TestWindow extends JFrame {
  public static final int SAME = 0;

  public static final int DIFFERENT = 1;

  private CustomPainter painter;

  private int result = -1;

  public TestWindow() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(640, 480);
    setLocationRelativeTo(null);
    initialize();
    setVisible(true);
  }

  private void initialize() {
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    JComponent java2d = new JPanel() {
      @Override
      public void paint(Graphics g) {
        super.paint(g);
        if (painter != null) {
          painter.paint((Graphics2D) g, false);
        }
      }
    };

    Component jogl = new JOGLPanel() {
      @Override
      public void paintGL(Graphics2D g) {
        if (painter != null) {
          painter.paint(g, true);
        }
      }
    };

    splitPane.setLeftComponent(jogl);
    splitPane.setRightComponent(java2d);
    splitPane.setResizeWeight(0.5);

    JButton sameButton = new JButton("Same");
    sameButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        result = SAME;
      }
    });

    JButton differentButton = new JButton("Different");
    differentButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        result = DIFFERENT;
      }
    });

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(sameButton);
    buttonPanel.add(differentButton);

    setLayout(new BorderLayout());
    add(splitPane, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        result = DIFFERENT;
      }
    });

    new Timer(200, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        repaint();
      }
    }).start();
  }

  public void setPainter(final Painter painter) {
    this.painter = new CustomPainter() {
      @Override
      public void paint(Graphics2D g2d, boolean jogl) {
        painter.paint(g2d);
      }
    };
  }

  public void setPainter(CustomPainter painter) {
    this.painter = painter;
  }

  public void close() {
    setVisible(false);
  }

  public void assertSame() throws InterruptedException {
    int result = waitForInput();
    Assert.assertEquals("User did not consider the two to be the same.", SAME, result);
  }

  public int waitForInput() throws InterruptedException {
    result = -1;
    while (result == -1) {
      Thread.sleep(100);
    }

    int value = result;
    result = -1;
    return value;
  }
}
