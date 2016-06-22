package nl.osrs.util.db;

public enum DataTable {
	NPC("npc"), SPAWN("npc_spawn"), ITEM("item");
	
	private String table;
	
	private DataTable(String table) {
		this.table = table;
	}
	
	public String getTable() {
		return table;
	}
}
