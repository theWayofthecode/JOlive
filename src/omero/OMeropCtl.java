package omero;

import java.nio.ByteOrder;

import org.apache.mina.common.ByteBuffer;

public class OMeropCtl extends OMerop {
	public static final byte type = 2;
	public String ctl;

	public OMeropCtl(ByteBuffer b) {
		super();
		unpack(b);
	}
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
		d.put(type);
		pstring(d, path);
		pstring(d, ctl);
		d.position(0);
		return d;
	}
	
	public int unpack(ByteBuffer f) {
		int initialPos = f.position();

		//message packed size
		int size = f.getInt();
		if (size > f.capacity()) {
			System.err.println("size > capacity in Update");
			return 0;
		}
		
		//type
		if (type != f.get()) {
			System.err.println("Wrong merop type (Not Update)");
			return 0;
		}
		
		path = gstring(f);
		ctl = gstring(f);
		return f.position() - initialPos;
	}

	public static void main(String[] args) {
		OMeropCtl mctl = new OMeropCtl("/main", "top");
		ByteBuffer bb = mctl.pack();
		System.err.println(bb.limit());
		byte[] b = new byte[21];
		bb.get(b);
		OUtils.HexDump(b, 0);
	}
	
	public String toString() {
		return (new String(path + "\n" +
							ctl + "\n"));
	}
}
