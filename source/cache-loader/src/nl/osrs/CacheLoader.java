package nl.osrs;

import javax.swing.JFrame;

import nl.osrs.rs.Model;
import nl.osrs.rs.RSImageProducer;
import nl.osrs.rs.Sprite;
import nl.osrs.rs.StreamLoader;
import nl.osrs.rs.definitions.EntityDef;
import nl.osrs.rs.definitions.ItemDef;
import nl.osrs.rs.definitions.ObjectDef;

public class CacheLoader {

	public static void loadConfigurations() {
		StreamLoader streamLoader = StreamLoader.getConfig();

		ItemDef.unpackConfig(streamLoader);
		EntityDef.unpackConfig(streamLoader);
		ObjectDef.unpackConfig(streamLoader);
	}

	public static void loadModels() {
		Model.method459();
	}

	/**
	 * This is currently used to test whether we can display an inventory sprite,
	 * i.e. a rendered drop model using the variables in ItemDef, in a JFrame.
	 * @param args Not used.
	 */
	public static void main(String[] args) {
		CacheLoader.loadModels();
		CacheLoader.loadConfigurations();
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(200, 200);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		RSImageProducer screen = new RSImageProducer(200, 200);
		
		Sprite sprite = ItemDef.getSprite(4151, 1, 0);
		sprite.drawSprite(0, 0);
		
		screen.drawGraphics(0, frame.getContentPane().getGraphics(), 0);
	}

}
