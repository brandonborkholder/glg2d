package glg2d.examples.shaders;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

@SuppressWarnings("serial")
public class UIDemo extends JPanel {
  public UIDemo() {
    JMenuBar menuBar;
    {
      menuBar = new JMenuBar();
      JMenu menu = new JMenu("File");
      menuBar.add(menu);
      JMenuItem item = new JMenuItem("Open ...");
      menu.add(item);
    }

    JTree tree;
    {
      DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
      root.add(new DefaultMutableTreeNode("A"));
      DefaultMutableTreeNode b = new DefaultMutableTreeNode("B");
      b.add(new DefaultMutableTreeNode("b"));
      b.add(new DefaultMutableTreeNode("beta"));
      root.add(b);
      root.add(new DefaultMutableTreeNode("C"));
      root.add(new DefaultMutableTreeNode("D"));
      tree = new JTree(new DefaultTreeModel(root));
    }

    JTable table;
    {
      DefaultTableModel model = new DefaultTableModel(new String[] {"a", "b", "c"}, 0);

      model.addRow(new Object[] {1, "a", "b"});
      model.addRow(new Object[] {3, "A", "t"});
      model.addRow(new Object[] {2, "D", "c"});
      model.addRow(new Object[] {4, "a", "b"});
      model.addRow(new Object[] {3, "D", "t"});
      model.addRow(new Object[] {9, "3", "l"});
      model.addRow(new Object[] {8, "a", "r"});
      model.addRow(new Object[] {2, "G", "k"});
      model.addRow(new Object[] {3, "f", "g"});
      table = new JTable(model);
    }

    JRadioButton radioButton = new JRadioButton("Radio");
    JCheckBox checkbox = new JCheckBox("Checkbox");
    JButton button = new JButton("Button");
    JComboBox box = new JComboBox(new String[] {"check", "box", "model"});

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(radioButton);
    buttonPanel.add(checkbox);
    buttonPanel.add(button);
    buttonPanel.setBorder(BorderFactory.createTitledBorder("Buttons"));

    JPanel boxPanel = new JPanel();
    boxPanel.add(box);
    boxPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(buttonPanel, BorderLayout.NORTH);
    rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);
    rightPanel.add(boxPanel, BorderLayout.SOUTH);

    setLayout(new BorderLayout());
//    add(menuBar, BorderLayout.NORTH);
    add(tree, BorderLayout.WEST);
    add(rightPanel, BorderLayout.CENTER);
  }
}
