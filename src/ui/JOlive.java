package ui;


import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import omero.OOlive;

import uk.ac.rdg.resc.jstyx.StyxException;
import uk.ac.rdg.resc.jstyx.client.CStyxFile;
import uk.ac.rdg.resc.jstyx.client.StyxConnection;



public class JOlive  extends JFrame {

	public JOlive(String ip, int port) {
		super();
		JPanel rootp = null;

		
    	setTitle("JOlive");
    	setSize(800, 600);
    	setLocationRelativeTo(null);
    	setDefaultCloseOperation(EXIT_ON_CLOSE);
    	
		try {
			StyxConnection conn = new StyxConnection(ip, port);
			conn.connect();		
			CStyxFile root = conn.getRootDirectory();
			(new OOlive(root.getFile("olive"))).start();
			rootp = new JPanel();
			CStyxFile main = root.getFile("main");
			JOPanel jp = new JOPanel(main, rootp);
			jp.createUITree(main, rootp);
		} catch (StyxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getContentPane().add(rootp);
		setVisible(true);
	}

	public static void main(String args[]) {
		JOlive jo = new JOlive("127.0.0.1", 4987);
	}
}
