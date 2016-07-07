package nl.osrs.test;

import org.junit.Test;

import junit.framework.TestCase;
import nl.osrs.CacheLoader;
import nl.osrs.rs.definitions.EntityDef;
import nl.osrs.rs.definitions.ItemDef;
import nl.osrs.rs.definitions.ObjectDef;

public class CacheLoaderTest extends TestCase {
	
	@Override
	public void setUp() {
		new CacheLoader();
	}

	@Test
	public void testItemDefinitionLoading() {
		ItemDef item = ItemDef.forID(11726);
		
		assertEquals(item.name, "Bandos tassets");
	}

	@Test
	public void testNpcDefinitionLoading() {
		EntityDef npc = EntityDef.forID(55);
		
		assertEquals(npc.name, "Blue dragon");
	}

	@Test
	public void testObjectDefinitionLoading() {
		ObjectDef object = ObjectDef.forID(26814);
		
		assertEquals(object.name, "Furnace");
	}

}
