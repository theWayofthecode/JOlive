package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


public class JDrawPanel extends JPanel {

	/*TODO: 
	 * Hash table for colors
	 * Adjust to preferred size
	 * Add support for other instructions too*/

	private static final int[] OSTROKE = {1, 3, 6, 12};
	public String[] instructions;
    public JDrawPanel(String data) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        instructions = data.split("\n");
    }

    public Dimension getPreferredSize() {
        return new Dimension(80, 80);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        
        for (int i = 0; i < instructions.length; i++) {
        	StringTokenizer instr = new StringTokenizer(instructions[i]);
        	String type = instr.nextToken();
        	if ("ellipse".equals(type)) {
        		ellipse(instr, g);
        	} else if ("fillellipse".equals(type)) {
        		fillellipse(instr, g);
        	} else if ("line".equals(type)) {
        		line(instr, g);
        	}
        }
    }
    
    private void ellipse(StringTokenizer instr, Graphics g) {
    	int cx = Integer.parseInt(instr.nextToken());
    	int cy = Integer.parseInt(instr.nextToken());
    	int rx = Integer.parseInt(instr.nextToken());
    	int ry = Integer.parseInt(instr.nextToken());
    	
    	cx = cx - rx;
    	cy = cy - ry;
    	rx <<= 1;
    	ry <<= 1;
    	
    	g.drawOval(cx, cy, rx, ry);
    }
    
    private void fillellipse(StringTokenizer instr, Graphics g) {
    	int cx = Integer.parseInt(instr.nextToken());
    	int cy = Integer.parseInt(instr.nextToken());
    	int rx = Integer.parseInt(instr.nextToken());
    	int ry = Integer.parseInt(instr.nextToken());
    	
    	//Have to use hashtable for color picking instead of if/else
    	if (instr.hasMoreTokens()) {
    		String col = instr.nextToken();
    		if ("back".equals(col)) {
    			g.setColor(Color.GRAY);
    		} else if ("mback".equals(col)) {
    			g.setColor(Color.DARK_GRAY);
    		}
    	}
    	cx = cx - rx;
    	cy = cy - ry;
    	rx <<= 1;
    	ry <<= 1;
    	
    	g.fillOval(cx, cy, rx, ry);
    	g.setColor(Color.BLACK);
    }
    
    private void line(StringTokenizer instr, Graphics g) {
       	int ax = Integer.parseInt(instr.nextToken());
    	int ay = Integer.parseInt(instr.nextToken());
    	int bx = Integer.parseInt(instr.nextToken());
    	int by = Integer.parseInt(instr.nextToken());
    	
    	//ea
    	instr.nextToken();
    	//eb
    	instr.nextToken();
    	int r = Integer.parseInt(instr.nextToken());
    	r = JDrawPanel.OSTROKE[r];
    	
    	Graphics2D g2D = (Graphics2D)g;
    	g2D.setStroke(new BasicStroke(r));
    	g.drawLine(ax, ay, bx, by);
    	g2D.setStroke(new BasicStroke(1));
    }
}
