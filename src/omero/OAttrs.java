package omero;

import java.util.StringTokenizer;

public class OAttrs {
	public boolean tag = false;
	public boolean show = false;
	public boolean layout = false;
	public String[] order;
	public boolean col = false;
	public boolean row = false;
	public int applid = 0;
	public int applpid = -1;
	public boolean clean = true;
	public char font = 'T';
	public int[] sel;
	public int mark = -1;
	public boolean scroll = false;
	public int tab = -1;
	public String[] attrsStr;
	
	public OAttrs() {
		sel = new int[2];
		sel[0] = sel[1] = -1;
		order = null;
		attrsStr = null;
	}
	
	public OAttrs(String astr) {
		this();

		attrsStr = astr.split("\n");
		for (String attr: attrsStr) {
			StringTokenizer st = new StringTokenizer(attr);
			String str = st.nextToken();
			
			//make sure to log invalid input
			if ("tag".equals(str)) {
				tag = true;
			} else if ("notag".equals(str)) {
				tag = false;
			} else if ("show".equals(str)) {
				show = true;				
			} else if ("layout".equals(str)) {
				layout = true;
			} else if ("col".equals(str)) {
				col = true;
			} else if ("row".equals(str)) {
				row = true;
			} else if ("order".equals(str)) {
				order = new String[st.countTokens()];
				int i = 0;
				while (st.hasMoreTokens())
					order[i++] = st.nextToken();
			} else if ("appl".equals(str)) {
				str = st.nextToken();
				applid = Integer.parseInt(str);
				str = st.nextToken();
				applpid = Integer.parseInt(str);
			} else if ("hide".equals(str)) {
				show = false;
			} else if ("clean".equals(str)) {
				clean = true;
			} else if ("dirty".equals(str)) {
				clean = false;
			} else if ("font".equals(str)) {
				str = st.nextToken();
				font = str.toCharArray()[0];
			} else if ("sel".equals(str)) {
				if (st.hasMoreTokens()) {
					str = st.nextToken();
					sel[0] = Integer.parseInt(str);
					str = st.nextToken();
					sel[1] = Integer.parseInt(str);
				}
			} else if ("scroll".equals(str)) {
				scroll = true;
			} else if ("noscroll".equals(str)) {
				scroll = false;
			} else if ("tab".equals(str)) {
				str = st.nextToken();
				tab = Integer.parseInt(str);
			} else {
				//report unknown attributes
			}			
		}
	}
		
	public String toString() {
		String toStr = "";
		for(String str: attrsStr)
			toStr += str + "\n";
		return toStr;
	}
}