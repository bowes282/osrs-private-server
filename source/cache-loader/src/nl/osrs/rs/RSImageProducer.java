package nl.osrs.rs;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public final class RSImageProducer {

	public final int[] canvasRaster;
	public final int canvasWidth;
	public final int canvasHeight;
	private final BufferedImage bufferedImage;

	public RSImageProducer(int canvasWidth, int canvasHeight) {
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
		canvasRaster = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
		initDrawingArea();
	}

	public void drawGraphics(int x, Graphics graphics, int y) {
		graphics.drawImage(bufferedImage, y, x, null);
	}

	public void initDrawingArea() {
		DrawingArea.initDrawingArea(canvasHeight, canvasWidth, canvasRaster);
	}
	
}