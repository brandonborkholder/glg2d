package glg2d.util;

import glg2d.G2DGLCanvas;

import java.awt.BorderLayout;
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

import org.junit.Assert;

@SuppressWarnings("serial")
public class TestWindow extends JFrame implements Tester {
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
    final JComponent java2d = new JPanel() {
      @Override
      public void paint(Graphics g) {
        super.paint(g);
        if (painter != null) {
          painter.paint((Graphics2D) g, false);
        }
      }
    };

    final JComponent jogl = new JPanel() {
      @Override
      public void paint(Graphics g) {
        super.paint(g);
        if (painter != null) {
          painter.paint((Graphics2D) g, true);
        }
      }
    };

    G2DGLCanvas canvas = new G2DGLCanvas(jogl);
    canvas.setGLDrawing(true);
    splitPane.setLeftComponent(canvas);
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

    new Timer(100, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        repaint();
      }
    }).start();
  }

  @Override
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

  @Override
  public void finish() {
    setVisible(false);
  }

  @Override
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
