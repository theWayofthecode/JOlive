package ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.StringTokenizer;

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
import omero.OMerop;
import omero.OMeropCtl;
import omero.OMeropUpdate;
import omero.OOlive;
import omero.OPanel;
import uk.ac.rdg.resc.jstyx.StyxException;
import uk.ac.rdg.resc.jstyx.client.CStyxFile;

public class JOPanel extends OPanel implements ActionListener, ItemListener {
	private JComponent swingComp;	
	private String sel;
	private String text;
	

	public JOPanel(CStyxFile sf, JComponent parentComp) {
		super(sf);
		initComponent();
		parentComp.add(this.swingComp);
		
		OOlive.panelRegistry.put(panelFd.getPath(), this);
		System.err.println("Registered as " + panelFd.getPath());
	}
	
	
	public void createUITree(CStyxFile cf, JComponent parComp) throws StyxException {
		if (!cf.isDirectory())
			return;
		JOPanel jop = new JOPanel(cf, parComp);
		for (CStyxFile f : cf.getChildren())
			createUITree(f, jop.getComponent());
	}

	public void removeUITree(CStyxFile cf) {
		try {
			if (!cf.isDirectory())
				return;
			for (CStyxFile f : cf.getChildren())
				removeUITree(f);
		} catch (StyxException e) {
			System.err.println("Perhaps file is not present. Removing JOPanel anyway.");
		}

		JOPanel jop = OOlive.panelRegistry.get(cf.getPath());
		OOlive.panelRegistry.remove(cf.getPath());
		System.err.println("Removed:" + cf.getPath());
		Container parent = jop.swingComp.getParent();
		parent.remove(jop.swingComp);
		parent.getParent().validate();
	}

	private void initComponent() {
		String type = getType();
		OAttrs attr = getAttrs();

		if ("col".equals(type) || attr.col) {
			swingComp = new JPanel();
			swingComp.setLayout(new BoxLayout(swingComp, BoxLayout.Y_AXIS));
		} else if ("row".equals(type) || attr.row) {
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
		swingComp.addNotify();
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
			} else if("Close".equals(selected)) {
				ctl("exec Close");
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
	
	public void omeroListener(OMerop e) {
		try {
			if (e.type == OMerop.UPDATETYPE) {
				omeroListenerUpdate((OMeropUpdate) e);
			} else if (e.type == OMerop.CTLTYPE) {
				omeroListenerCtl((OMeropCtl) e);
			}
		} catch (StyxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void omeroListenerCtl(OMeropCtl e) {
		System.err.println("Ctl:");
		System.err.println(e.toString());
		StringTokenizer parser = new StringTokenizer(e.ctl);
		String s = parser.nextToken();
		try {
			if ("close".equals(s)) {
				omeroListenerCtlClose();
			}
		} catch (StyxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void omeroListenerCtlClose() throws StyxException {
		removeUITree(panelFd);
	}

	private void omeroListenerUpdate(OMeropUpdate e) throws StyxException {
		System.err.println("Update:");
		System.err.println(e.toString());
		if (e.ctls == null)
			return;

		StringTokenizer parser = new StringTokenizer(e.ctls);
		String s = parser.nextToken();
		if ("order".equals(s)) {
			omeroListenerUpdateOrder(parser);
		}	
	}

	private void omeroListenerUpdateOrder(StringTokenizer order) throws StyxException {
		while (order.hasMoreTokens()) {
			String s = order.nextToken();
			CStyxFile f = panelFd.getFile(s);
			if (!OOlive.panelRegistry.containsKey(f.getPath())) {
				createUITree(f, swingComp);
				swingComp.revalidate();
			}
		}
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
