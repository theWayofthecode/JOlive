package omero;

import org.apache.mina.common.ByteBuffer;

import uk.ac.rdg.resc.jstyx.StyxException;
import uk.ac.rdg.resc.jstyx.StyxUtils;
import uk.ac.rdg.resc.jstyx.client.CStyxFile;
import uk.ac.rdg.resc.jstyx.client.StyxConnection;

public class OOlive {

	public OOlive(CStyxFile oliveFD) {
		OMeropCtl top = new OMeropCtl("/main", "top");
		byte[] td = new byte[top.packedsize()];
		top.pack().get(td);
		try {
			oliveFD.open(StyxUtils.ORDWR | StyxUtils.OTRUNC);
			oliveFD.writeAll(td, td.length);
			for (;;) {
				ByteBuffer bf = oliveFD.read(0);
				if (bf.get(bf.position() + 4) == OMeropUpdate.type) {
					OMeropUpdate u = new OMeropUpdate(bf);
					System.err.println(u.toString());
				} else if (bf.get(bf.position() + 4) == OMeropCtl.type) {
					OMeropCtl c = new OMeropCtl(bf);
					System.err.println(c.toString());
				} else {
					System.err.println("unkown type");
				}
			}
		} catch (StyxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			StyxConnection conn = new StyxConnection("127.0.0.1", 4987);
			conn.connect();		
			CStyxFile root = conn.getRootDirectory();
			OOlive oo = new OOlive(root.getFile("olive"));
		} catch (StyxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
