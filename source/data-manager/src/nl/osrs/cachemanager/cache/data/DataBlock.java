package nl.osrs.cachemanager.cache.data;

public class DataBlock {
	private int dataBlockId;
	private int nextDataBlockFileIndex;
	private int fileDataBlockId;
	private int nextDataBlockId;
	private IndexType nextFileType;
	
	private byte[] data;
	private int dataSize;
	
	public DataBlock(int dataBlockId, int nextDataBlockFileIndex, int fileDataBlockId, int nextDataBlockId, int nextFileType, byte[] data, int dataSize) {
		this.setDataBlockId(dataBlockId);
		this.setNextDataBlockFileIndex(nextDataBlockFileIndex);
		this.setFileDataBlockId(fileDataBlockId);
		this.setNextDataBlockId(nextDataBlockId);
		this.setNextFileType(nextFileType);
		this.setData(data);
		this.setDataSize(dataSize);
	}
	
	public void print() {
		System.out.println("Printing DataBlock " + dataBlockId + " {");
		System.out.print("\t\"Next data block index entry\":");
		System.out.println(nextDataBlockFileIndex);
		System.out.print("\t\"Current data block sequence id\":");
		System.out.println(fileDataBlockId);
		System.out.print("\t\"Next data block offset\":");
		System.out.println(nextDataBlockId);
		System.out.print("\t\"Next data block index type\":");
		System.out.println(nextFileType.name());
		System.out.print("\t\"Data size\":");
		System.out.println(dataSize);
		System.out.println("}");
	}

	public int getDataBlockId() {
		return dataBlockId;
	}

	public void setDataBlockId(int dataBlockId) {
		this.dataBlockId = dataBlockId;
	}

	public int getNextDataBlockFileIndex() {
		return nextDataBlockFileIndex;
	}

	public void setNextDataBlockFileIndex(int nextDataBlockFileIndex) {
		this.nextDataBlockFileIndex = nextDataBlockFileIndex;
	}

	public int getFileDataBlockId() {
		return fileDataBlockId;
	}

	public void setFileDataBlockId(int fileDataBlockId) {
		this.fileDataBlockId = fileDataBlockId;
	}

	public int getNextDataBlockId() {
		return nextDataBlockId;
	}

	public void setNextDataBlockId(int nextDataBlockId) {
		this.nextDataBlockId = nextDataBlockId;
	}

	public IndexType getNextFileType() {
		return nextFileType;
	}

	public void setNextFileType(int nextFileType) {
		this.nextFileType = IndexType.getIndexType(nextFileType - 1);
	}
	
	public void setNextFileType(IndexType nextFileType) {
		this.nextFileType = nextFileType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getDataSize() {
		return dataSize;
	}

	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}
}
