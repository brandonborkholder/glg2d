package glg2d;

import glg2d.misc.InstrumentPaint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

@SuppressWarnings("serial")
public class UIDemo extends JPanel {
  public UIDemo() {
    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.add(new JScrollPane(createTreeComponent()), BorderLayout.NORTH);
    leftPanel.add(new JScrollPane(createTableComponent()), BorderLayout.CENTER);
    leftPanel.add(new JScrollPane(createListComponent()), BorderLayout.SOUTH);

    JSplitPane mainSplit = new JSplitPane();
    mainSplit.setDividerSize(8);
    mainSplit.setOneTouchExpandable(true);
    mainSplit.setContinuousLayout(true);
    mainSplit.setLeftComponent(leftPanel);

    JPanel rightPanel = new JPanel(new BorderLayout());
    mainSplit.setRightComponent(rightPanel);
    rightPanel.add(createButtonComponent(), BorderLayout.NORTH);

    JPanel rightSubPanel = new JPanel(new BorderLayout());
    rightPanel.add(rightSubPanel, BorderLayout.CENTER);
    
    JPanel rightSubPanel2 = new JPanel(new BorderLayout());
    rightSubPanel2.add(createInputComponent(), BorderLayout.NORTH);
    rightSubPanel2.add(createMDIComponent(), BorderLayout.CENTER);

    JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    rightSplit.setDividerSize(10);
    rightSplit.setDividerLocation(400);
    rightSplit.setOneTouchExpandable(true);
    rightSplit.setTopComponent(rightSubPanel2);
    rightSplit.setBottomComponent(createBorderComponent());

    rightSubPanel.add(createProgressComponent(), BorderLayout.NORTH);
    rightSubPanel.add(rightSplit, BorderLayout.CENTER);
    rightSubPanel.add(createTabComponent(), BorderLayout.SOUTH);

    setLayout(new BorderLayout());
    mainSplit.setDividerLocation(300);
    add(mainSplit, BorderLayout.CENTER);
  }

  JComponent createTabComponent() {
    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Tab 1", new JLabel("Foo bar"));
    tabs.addTab("Tab 2", new JLabel("Foo bar"));
    tabs.addTab("Tab 3", new JLabel("Foo bar"));
    tabs.addTab("Tab 4", new JLabel("Foo bar"));
    tabs.addTab("Tab 5", new JLabel("Foo bar"));
    return tabs;
  }

  JComponent createProgressComponent() {
    JPanel panel = new JPanel();

    JProgressBar bar = new JProgressBar();
    bar.setIndeterminate(true);
    panel.add(bar);

    final JProgressBar bbar = new JProgressBar();
    panel.add(bbar);
    Timer timer = new Timer(100, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int value = bbar.getValue() + 1;
        value %= bbar.getMaximum();
        bbar.setValue(value);
      }
    });
    timer.setRepeats(true);
    timer.start();

    return panel;
  }

  JComponent createInputComponent() {
    JPanel panel = new JPanel();

    JComboBox box = new JComboBox(new String[] { "alpha", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel" });
    panel.add(box);

    JSpinner spinner = new JSpinner(new SpinnerNumberModel());
    panel.add(spinner);

    spinner = new JSpinner(new SpinnerDateModel());
    panel.add(spinner);

    JTextField field = new JTextField();
    field.setText("lorem ipsum...");
    field.setColumns(10);
    panel.add(field);

    JTextArea area = new JTextArea();
    area.setText("lorem ipsum... lorem ipsum lorem ipsum...\n lorem ipsum...\n lorem ipsum...");
    area.setColumns(10);
    area.setRows(3);
    panel.add(new JScrollPane(area));

    JPanel p = new JPanel();
    p.setPreferredSize(new Dimension(100, 50));
    panel.add(new JScrollPane(p, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS));

    JSlider slider = new JSlider();
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    panel.add(slider);

    return panel;
  }

  JComponent createBorderComponent() {
    JPanel panel = new JPanel();

    JLabel label = new JLabel("no border");
    panel.add(label);

    label = new JLabel("lowered bevel border");
    label.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    panel.add(label);

    label = new JLabel("raised bevel border");
    label.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    panel.add(label);

//    label = new JLabel("dashed border");
//    label.setBorder(BorderFactory.createDashedBorder(Color.red));
//    panel.add(label);

    label = new JLabel("5px empty border");
    label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    panel.add(label);

    label = new JLabel("raised etched border");
    label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
    panel.add(label);

    label = new JLabel("lowered etched border");
    label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    panel.add(label);

    label = new JLabel("line border");
    label.setBorder(BorderFactory.createLineBorder(Color.blue, 3));
    panel.add(label);

//    label = new JLabel("soft raised bevel border");
//    label.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED));
//    panel.add(label);

//    label = new JLabel("stroke border");
//    label.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 3)));
//    panel.add(label);

    label = new JLabel("title border");
    label.setBorder(BorderFactory.createTitledBorder("Title"));
    panel.add(label);

    label = new JLabel("compound border");
    label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createTitledBorder("Title")));
    panel.add(label);

    return panel;
  }
  
  JComponent createMDIComponent() {
    JDesktopPane pane = new JDesktopPane();
    pane.setDragMode(JDesktopPane.LIVE_DRAG_MODE);
    pane.setBackground(new JPanel().getBackground());
    pane.setOpaque(true);
    pane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

    for (int i = 0; i < 3; i++) {
      JInternalFrame frame1 = new JInternalFrame("Foo" + i, true);
      frame1.setClosable(true);
      frame1.setMaximizable(true);
      pane.add(frame1);

      JMenuBar bar = new JMenuBar();
      JMenu menu = new JMenu("File");
      menu.add(new JMenuItem("Open"));
      menu.add(new JMenuItem("Close"));
      bar.add(menu);
      frame1.setJMenuBar(bar);
      
      frame1.setSize(200, 200);
      frame1.setLocation(100 * i, 5 * i);
      frame1.setVisible(true);
    }
    
    return pane;
  }

  JComponent createListComponent() {
    DefaultListModel model = new DefaultListModel();
    model.addElement("alpha");
    model.addElement("bravo");
    model.addElement("charlie");
    model.addElement("delta");
    model.addElement("echo");
    model.addElement("foxtrot");
    model.addElement("golf");
    model.addElement("hotel");
    model.addElement("india");
    model.addElement("juliet");
    model.addElement("kilo");
    model.addElement("limo");
    model.addElement("mike");
    model.addElement("november");
    return new JList(model);
  }

  JComponent createTreeComponent() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
    root.add(new DefaultMutableTreeNode("A"));
    DefaultMutableTreeNode b = new DefaultMutableTreeNode("B");
    b.add(new DefaultMutableTreeNode("b"));
    b.add(new DefaultMutableTreeNode("beta"));
    root.add(b);
    root.add(new DefaultMutableTreeNode("C"));
    root.add(new DefaultMutableTreeNode("D"));
    return new JTree(new DefaultTreeModel(root));
  }

  JComponent createButtonComponent() {
    JPanel panel = new JPanel();

    JButton button = new JButton("Normal");
    panel.add(button);

//    button = new JButton("Text w/ border");
//    button.setBorderPainted(true);
//    button.setBorder(BorderFactory.createLoweredSoftBevelBorder());
//    panel.add(button);

    button = new JButton("Rollover icon");
    button.setRolloverIcon(getIcon("plus"));
    button.setIcon(getIcon("check"));
    button.setPressedIcon(getIcon("x"));
    panel.add(button);

    JRadioButton radio = new JRadioButton("Normal");
    panel.add(radio);

    JCheckBox check = new JCheckBox("Normal");
    panel.add(check);

    return panel;
  }

  JComponent createTableComponent() {
    DefaultTableModel model = new DefaultTableModel(new String[] { "a", "b", "c" }, 0);

    model.addRow(new Object[] { 1, "a", "b" });
    model.addRow(new Object[] { 3, "A", "t" });
    model.addRow(new Object[] { 2, "D", "c" });
    model.addRow(new Object[] { 4, "a", "b" });
    model.addRow(new Object[] { 3, "D", "t" });
    model.addRow(new Object[] { 9, "3", "l" });
    model.addRow(new Object[] { 8, "a", "r" });
    model.addRow(new Object[] { 2, "G", "k" });
    model.addRow(new Object[] { 3, "f", "g" });

    return new JTable(model);
  }

  Icon getIcon(String name) {
    try {
      InputStream str = UIDemo.class.getClassLoader().getResourceAsStream(name + ".png");
      return new ImageIcon(ImageIO.read(str));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws Exception {
    InstrumentPaint.instrument();
    
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    JFrame frame = new JFrame("Swing Demo");

//     frame.setContentPane(new UIDemo());
    frame.setContentPane(new G2DGLPanel(new UIDemo()));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(1024, 768));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
