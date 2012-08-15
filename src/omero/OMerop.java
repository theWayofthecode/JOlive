package omero;

import java.nio.charset.Charset;

import org.apache.mina.common.ByteBuffer;

public abstract class OMerop {
	protected final int BIT8SZ = 1;
	protected final int BIT32SZ = 4;
	protected final int NODATA = ~0;
	protected final int HDRSZ = BIT32SZ+BIT8SZ;	
	protected Charset utf8charset;
	public String path;
	
	public OMerop() {
		utf8charset = Charset.forName("UTF-8");
		path = null;
	}

	public OMerop(String path) {
		this();
		this.path = new String(path);
	}

	protected abstract int packedsize();
	public abstract ByteBuffer pack();
}
