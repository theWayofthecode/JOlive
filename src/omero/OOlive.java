package omero;

import java.util.HashMap;

import org.apache.mina.common.ByteBuffer;

import ui.JOPanel;
import uk.ac.rdg.resc.jstyx.StyxException;
import uk.ac.rdg.resc.jstyx.StyxUtils;
import uk.ac.rdg.resc.jstyx.client.CStyxFile;
import uk.ac.rdg.resc.jstyx.client.StyxConnection;

public class OOlive extends Thread {

	/* Keep in mind the option to abstract JOPanel to an interface to avoid
	 * unnecessary dependency
	 */
	public static HashMap<String, JOPanel> panelRegistry;

	CStyxFile oliveFD = null;
	
	public OOlive(CStyxFile oliveFD) {
		this.oliveFD = oliveFD;
		panelRegistry = new HashMap<String, JOPanel>();
	}
	
	public void run() {
		System.err.println("OOlive started");
		OMeropCtl top = new OMeropCtl("/main", "top");
		byte[] td = new byte[top.packedsize()];
		top.pack().get(td);
		try {
			oliveFD.open(StyxUtils.ORDWR | StyxUtils.OTRUNC);
			oliveFD.writeAll(td, td.length);

		} catch (StyxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (;;) {
			ByteBuffer bf = null;
			try {
				bf = oliveFD.read(0);
			} catch (StyxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.err.println(bf.toString());
			
			OMerop e = null;
			if (bf.get(bf.position() + 4) == OMerop.UPDATETYPE) {
				e = new OMeropUpdate(bf);
			} else if (bf.get(bf.position() + 4) == OMerop.CTLTYPE) {
				e = new OMeropCtl(bf);
			} else {
				System.err.println("unkown type");
			}
			
			System.err.println("OOlive received:" + e.toString());
			JOPanel jp = panelRegistry.get(e.path);
			if (jp != null) {
				System.err.println("Calling omeroListener...");
				jp.omeroListener(e);
			} else {
				System.err.println("OOlive jp null");
			}
		}
	}

	public static void main(String[] args) {
		try {
			StyxConnection conn = new StyxConnection("127.0.0.1", 4987);
			conn.connect();		
			CStyxFile root = conn.getRootDirectory();
			OOlive oo = new OOlive(root.getFile("olive"));
			oo.start();
		} catch (StyxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
