package omero;



import org.apache.mina.common.ByteBuffer;

public class OMeropUpdate extends OMerop {
	public static final byte type = 1;
	public int vers;
	public String ctls;
	public byte[] data;
	public String edits;

	public OMeropUpdate() {
		super();
	}
	
	public OMeropUpdate(ByteBuffer f) {
		super();
		unpack(f);
	}

	@Override
	protected int packedsize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ByteBuffer pack() {
		// TODO Auto-generated method stub
		return null;
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
		vers = f.getInt();
		ctls = gstring(f);
		
		//data
		int ds = f.getInt();
		if (ds != NODATA) {
			data = new byte[ds];
			f.get(data);
		}
		
		edits = gstring(f);
		return f.position() - initialPos;
	}
	
	public String toString() {
		return (new String(path + "\n" +
							vers + "\n" +
							ctls + "\n" +
							edits + "\n"));
	}
}
