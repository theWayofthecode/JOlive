package omero;

import java.nio.ByteOrder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.common.ByteBuffer;

public class OMeropCtl extends OMerop {
	private final byte type = 2;
	public String ctl;

	public OMeropCtl(String path, String ctl) {
		super(path);
		this.ctl = new String( ctl);
	}
	@Override
	protected int packedsize() {
		int ml = HDRSZ;
		ml += BIT32SZ + path.length();
		ml += BIT32SZ + ctl.length();
		return ml;
	}

	@Override
	public ByteBuffer pack() {
		int ds = this.packedsize();
		ByteBuffer d = ByteBuffer.allocate(ds, true);
		d.order(ByteOrder.LITTLE_ENDIAN);

		d.putInt(ds);
		d.put(this.type);
		d.putInt(path.length());
		try {
			d.putString(path, utf8charset.newEncoder());
			d.putInt(ctl.length());
			d.putString(ctl, utf8charset.newEncoder());
		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		d.position(0);
		return d;
	}

	public static void main(String[] args) {
		OMeropCtl mctl = new OMeropCtl("/main", "top");
		ByteBuffer bb = mctl.pack();
		byte[] b = new byte[21];
		bb.get(b);
		OUtils.HexDump(b, 0);
	}
}
