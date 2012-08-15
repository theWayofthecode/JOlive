package omero;

public final class OUtils {

	public static void HexDump(byte[] data, int offset) {
		for (int i = offset; i < data.length; i++) {
			System.err.printf("%x ", data[i]);
		}
		System.err.println(" $");
	}
}
