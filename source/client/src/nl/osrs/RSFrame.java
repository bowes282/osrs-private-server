package nl.osrs;
import java.awt.*;

final class RSFrame extends Frame {
	private boolean antiAliasing = false;
	
	private static final long serialVersionUID = 6411404177667816019L;

	public RSFrame(RSApplet RSApplet_, int i, int j) {
		rsApplet = RSApplet_;
		setTitle(Client.clientName);
		setResizable(false);
		setVisible(true);
		toFront();
		setSize(i + 8, j + 28);
		setResizable(true);
		setLocationRelativeTo(null);
	}

	public Graphics getGraphics() {
		Graphics g = super.getGraphics();
		g.translate(4, 24);
		return g;
	}

	public void update(Graphics g) {
		rsApplet.update(g);
	}

	public void paint(Graphics g) {
		if (antiAliasing) {
	        Graphics2D g2 = (Graphics2D)rsApplet.graphics;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                             RenderingHints.VALUE_ANTIALIAS_ON);
	        rsApplet.paint(g2);
		} else
			rsApplet.paint(g);
	}

	private final RSApplet rsApplet;
}
