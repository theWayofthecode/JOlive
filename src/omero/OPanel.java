package omero;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import uk.ac.rdg.resc.jstyx.client.*;
import uk.ac.rdg.resc.jstyx.*;

public class OPanel {
	private int id = -1;
	private String name;
	private CStyxFile panelFd;	
	private CStyxFile cFd;
	private CStyxFile dFd;
	
	public OPanel(CStyxFile ref) {
		try {
			openPanel(ref);
		} catch (StyxException e) {
			System.err.println("Failed to open Panel" + ref.getName());
			e.printStackTrace();
		}		
	}
	
	public OPanel(String name, int id) {
		
	}
	
	/**
	 * perhaps to create
	 */
	public OPanel(CStyxFile parent, String name) {
		panelFd = parent.getFile(name);
		try {
			panelFd.open(StyxUtils.OREAD);
			cFd = panelFd.openFile("ctl", StyxUtils.OWRITE | StyxUtils.ORCLOSE);
		} catch (StyxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void ctl(String ctlRequest) {
		try {
			cFd.write(ctlRequest.getBytes(), 0, false);
		} catch (StyxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public OAttrs getAttrs() {
		String attrsStr = "";
		try {
			CStyxFile ctl = panelFd.openFile("ctl", StyxUtils.OREAD);
			attrsStr = ctl.getContents();
		} catch (StyxException e) {
			System.err.println("Failed to open ctl, while trying to read attributes");
			e.printStackTrace();
		}
		return new OAttrs(attrsStr);
	}
	
	public void close() {
		
	}
	
	public String toString() {
		return panelFd.toString();
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return name.split(":")[0];
	}
	
	/**
	 * Possible optimization required. Probably large file can be encountered
	 * @return all the contents of the data file as a String
	 */
	public String getData() {
		String data = "";
		
		try {
			dFd.open(StyxUtils.OREAD | StyxUtils.ORCLOSE);
			File dataFile = new File(name);
			dFd.download(dataFile);			
			byte[] dataRaw = new byte[(int)dataFile.length()];
			(new FileInputStream(dataFile)).read(dataRaw);
			data = new String(dataRaw);
			dFd.close();
		} catch (StyxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.err.println("Data downloaded file not found");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return data;
	}
	
	public void setData(String data) {
		try {
			dFd.open(StyxUtils.OWRITE | StyxUtils.OTRUNC);
			dFd.writeAll(data.getBytes(), 0);
			dFd.close();
		} catch (StyxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void openPanel(CStyxFile ref) throws StyxException {
		panelFd = ref;
		name = ref.getName();		
		panelFd.open(StyxUtils.OREAD);
		cFd = panelFd.openFile("ctl", StyxUtils.OWRITE | StyxUtils.ORCLOSE);
 		dFd = panelFd.getFile("data");
/*		if (dFd.exists()) {
			dFd.open(StyxUtils.ORDWR | StyxUtils.ORCLOSE);
		} else {
			dFd = null;
		} */
	}
}