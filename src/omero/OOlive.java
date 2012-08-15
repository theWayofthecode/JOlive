package omero;

import org.apache.mina.common.ByteBuffer;

import uk.ac.rdg.resc.jstyx.StyxException;
import uk.ac.rdg.resc.jstyx.StyxUtils;
import uk.ac.rdg.resc.jstyx.client.CStyxFile;
import uk.ac.rdg.resc.jstyx.client.StyxConnection;

public class OOlive {

	static final private byte[] handshakeData = {0x15, 0x0, 0x0, 0x0, 0x2, 0x5, 0x0, 0x0, 0x0, 0x2f, 0x6d,
										0x61, 0x69, 0x6e, 0x3, 0x0, 0x0, 0x0, 0x74, 0x6f, 0x70};

	public OOlive(CStyxFile oliveFD) {
		try {
			oliveFD.open(StyxUtils.ORDWR | StyxUtils.OTRUNC);
			oliveFD.writeAll(handshakeData, handshakeData.length);
			for (;;) {
				ByteBuffer bf = oliveFD.read(0);
				System.out.println(bf.toString());
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
