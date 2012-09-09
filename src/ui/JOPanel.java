package ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
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
	/*TODO:
	 * Check if mouse listener can move to a seperate class
	 */
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

	/*TODO:
	 * break this function to subfunctions
	 */
	private void initComponent() {
		String type = getType();
		OAttrs attr = getAttrs();

		if ("col".equals(type) || attr.col) {
			swingComp = new JPanel();
			swingComp.setLayout(new BoxLayout(swingComp, BoxLayout.Y_AXIS));
			swingComp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		} else if ("row".equals(type) || attr.row) {
			swingComp = new JPanel();
			swingComp.setLayout(new BoxLayout(swingComp, BoxLayout.X_AXIS));
			swingComp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
		} else if ("draw".equals(type)) {
			JDrawPanel jdp = new JDrawPanel(getData());
			swingComp = jdp;
		}
		
		if (attr.font == 'B') {
			modifyFont(Font.BOLD);
		} else if (attr.font == 'I') {
			modifyFont(Font.ITALIC);
		} else if (attr.tag == true) {
			JLabel tag = new JLabel(new ImageIcon(getClass().getResource("/resources/tag.png")));
			tag.addMouseListener(createPopupMenu());
			swingComp.add(tag);
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

		if ("close".equals(s)) {
			removeUITree(panelFd);
		} else if ("focus".equals(s)) {
			omeroListenerCtlFocus();
		}

	}

	private void omeroListenerUpdate(OMeropUpdate e) throws StyxException {
		System.err.println("Update:");
		System.err.println(e.toString());

		if (e.ctls == null) {
			if ("draw".equals(getType())) {
				omeroListenerUpdateDraw();
			}
		} else {
			StringTokenizer parser = new StringTokenizer(e.ctls);
			String s = parser.nextToken();
			if ("order".equals(s)) {
				omeroListenerUpdateOrder(parser);
			} else if ("font".equals(s)) {
				omeroListenerUpdateFont(parser);
			} else if ("hide".equals(s)) {
				swingComp.setVisible(false);
			} else if ("show".equals(s)) {
				swingComp.setVisible(true);
			}
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
	
	private void omeroListenerUpdateFont(StringTokenizer font) {
		String type = font.nextToken();
		System.err.println("type" + type);

		if ("B".equals(type)) {
			modifyFont(Font.BOLD);
		} else if ("I".equals(type)) {
			modifyFont(Font.ITALIC);
		}
	}

	private void omeroListenerUpdateDraw() {
		JDrawPanel jdp = (JDrawPanel)swingComp;
		jdp.instructions = getData().split("\n");
		jdp.updateUI();
	}

	private void omeroListenerCtlFocus() {
		swingComp.grabFocus();
	}
	public JComponent getComponent() {
		return swingComp;
	}

	private void modifyFont(int style) {
		JTextComponent t;
		try {
			t = (JTextComponent)this.swingComp;
		} catch (ClassCastException e) {
			System.err.println(e.getMessage());
			return;
		}
		Font f = t.getFont();
		t.setFont(new Font(f.getName(), style, f.getSize()));
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
			String type = getType();
			if ("text".equals(type) || "label".equals(type)
					|| "tag".equals(type) || "tbl".equals(type)) {
				JTextComponent tc = (JTextComponent) e.getComponent();
				sel = tc.getSelectedText();
				text = tc.getText();
			}
			if (e.isPopupTrigger()) {
				popup.show(swingComp, e.getX(), e.getY());
			}
		}
	}
}
