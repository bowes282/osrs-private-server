package nl.osrs.cachemanager.cache.data;

public enum IndexType {
	ARCHIVES(0), MODELS(1), ANIMATIONS(2), SOUNDS(3), MAPS(4);
	
	private int index;
	
	private IndexType(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static IndexType getIndexType(int index) {
		for (IndexType type : IndexType.values())
			if (type.getIndex() == index)
				return type;
		return null;
	}
}
