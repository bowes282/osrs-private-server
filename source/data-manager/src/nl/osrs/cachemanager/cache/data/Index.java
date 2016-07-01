package nl.osrs.cachemanager.cache.data;

public class Index {
	private int length;
	private int initialDataBlockId;
	private int offset;
	private IndexType type;
	
	public Index(int length, int initialDataBlockId, int offset, IndexType type) {
		this.setLength(length);
		this.setInitialDataBlockId(initialDataBlockId);
		this.setOffset(offset);
		this.setType(type);
	}
	
	public void print() {
		System.out.println("Printing Index " + offset + " {");
		System.out.print("\t\"Data length:\"");
		System.out.println(length);
		System.out.print("\t\"Initial data block id:\"");
		System.out.println(initialDataBlockId);
		System.out.println("}");
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getInitialDataBlockId() {
		return initialDataBlockId;
	}

	public void setInitialDataBlockId(int initialDataBlockId) {
		this.initialDataBlockId = initialDataBlockId;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public IndexType getType() {
		return type;
	}

	public void setType(IndexType type) {
		this.type = type;
	}
	
}
