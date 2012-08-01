package ui;


import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.ac.rdg.resc.jstyx.StyxException;
import uk.ac.rdg.resc.jstyx.client.CStyxFile;
import uk.ac.rdg.resc.jstyx.client.StyxConnection;



public class JOlive  extends JFrame {

	public JOlive(String ip, int port) {
    	setTitle("JOlive");
    	setSize(800, 600);
    	setLocationRelativeTo(null);
    	setDefaultCloseOperation(EXIT_ON_CLOSE);
    	
		try {
			StyxConnection conn = new StyxConnection(ip, port);
			conn.connect();		
			CStyxFile root = conn.getRootDirectory();
			JPanel rootP = new JPanel();
			initUItree(root.getFile("main"), rootP);
			getContentPane().add(rootP);
		} catch (StyxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void initUItree(CStyxFile cf, JComponent parComp) throws StyxException {
		assert(parComp != null);
    	for (CStyxFile f: cf.getChildren()) {
    		if (f.isDirectory()) {
        		JOPanel jop = new JOPanel(f, parComp);
    			initUItree(f, jop.getComponent());
    		}
    	}
	}

	public static void main(String args[]) {
		JOlive jo = new JOlive("127.0.0.1", 4987);
		jo.setVisible(true);
	}
}
