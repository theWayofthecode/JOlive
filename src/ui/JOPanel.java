package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import omero.OAttrs;
import omero.OPanel;
import uk.ac.rdg.resc.jstyx.client.CStyxFile;

public class JOPanel extends OPanel implements ActionListener, ItemListener{
	private JComponent swingComp;	
	private String sel;
	private String text;
	
	public JOPanel(CStyxFile sf, JComponent parentComp) {
		super(sf);
		System.out.println(getName());
		initComponent();
		parentComp.add(this.swingComp);
	}
	
	private void initComponent() {
		String type = getType();
		System.out.println("type " + type);

		if ("col".equals(type)) {
			swingComp = new JPanel();
			swingComp.setLayout(new BoxLayout(swingComp, BoxLayout.Y_AXIS));
		} else if ("row".equals(type)) {
			swingComp = new JPanel();
			swingComp.setLayout(new BoxLayout(swingComp, BoxLayout.X_AXIS));
		} else if ("text".equals(type)) {
			JTextArea jta = new JTextArea(getData());
			jta.addMouseListener(createPopupMenu());
			swingComp = jta;
		} else if ("label".equals(type)) {
			JLabel jl = new JLabel(getData());
			jl.addMouseListener(createPopupMenu());
			swingComp = jl;
		} else if ("button".equals(type)) {
			JButton jb = new JButton(getName().split(":")[1]);
			jb.addActionListener(this);
			swingComp = jb;
		} else if ("tag".equals(type)) {
			JTextField jtf = new JTextField(getData());
			jtf.addMouseListener(createPopupMenu());
			swingComp = jtf;
		} else if ("tbl".equals(type)) {
			JTextArea jta = new JTextArea(getData());
			jta.addMouseListener(createPopupMenu());
			swingComp = jta;
		}
	}
		
//		if (attrs.tag)
//			jp.add(new JButton(getName(), new ImageIcon(getClass().getResource("tag.png"))));

	public void actionPerformed(ActionEvent e) {
		String className = e.getSource().getClass().getName();
		if ("javax.swing.JMenuItem".equals(className)) {
			JMenuItem source = (JMenuItem)(e.getSource());
			String selected = source.getText();

			if ("Exec".equals(selected)) {        	
				ctl("exec " + sel);
			} else if("Write".equals(selected)) {
				setData(text);
			}
		} else if ("javax.swing.JButton".equals(className)) {
			JButton source = (JButton)(e.getSource());
			ctl("exec " + source.getText());
		}
	}
	
	public void itemStateChanged(ItemEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String s = "Item event detected.\n" + source.getText();
        System.err.println(s);
	}
	
	public MouseListener createPopupMenu() {
        JMenuItem menuItem;
 
        //Create the popup menu.
        JPopupMenu popup = new JPopupMenu();
        menuItem = new JMenuItem("Write");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        
        menuItem = new JMenuItem("Paste");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        
        menuItem = new JMenuItem("Open");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        
        menuItem = new JMenuItem("Find");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        
        menuItem = new JMenuItem("Exec");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        
        menuItem = new JMenuItem("Close");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        
        //Add listener to the text area so the popup menu can come up.
        MouseListener popupListener = new PopupListener(popup);
        return popupListener;
    }
	
	public JComponent getComponent() {
		return swingComp;
	}
	
	class PopupListener extends MouseAdapter {
		JPopupMenu popup;

		PopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			JTextComponent tc = (JTextComponent) e.getComponent();
			sel = tc.getSelectedText();
			text = tc.getText();
			if (e.isPopupTrigger()) {
				popup.show(tc, e.getX(), e.getY());
			}
		}
	}
}