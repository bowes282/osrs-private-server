package nl.osrs.cachemanager.cache.data;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

public class DataPacker {
	private RandomAccessFile dataFile;
	private RandomAccessFile indexFile;
	private IndexType type;

	private byte[] buffer = new byte[520];

	public DataPacker(RandomAccessFile dataFile, RandomAccessFile indexFile, IndexType type) {
		this.dataFile = dataFile;
		this.indexFile = indexFile;
		this.type = type;
	}
	
	public synchronized byte[] decompress(int i) {
		try {
			indexFile.seek(i * 6);
			int l;
			for(int j = 0; j < 6; j += l)
			{
				l = indexFile.read(buffer, j, 6 - j);
				if(l == -1)
					return null;
			}
			int i1 = ((buffer[0] & 0xff) << 16) + ((buffer[1] & 0xff) << 8) + (buffer[2] & 0xff);
			int j1 = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);
			//if(i1 < 0 || i1 > 0xffffff)
			//	return null;
			if(j1 <= 0 || (long)j1 > dataFile.length() / 520L)
				return null;
			byte abyte0[] = new byte[i1];
			int k1 = 0;
			for(int l1 = 0; k1 < i1; l1++) {
				if(j1 == 0)
					return null;
				dataFile.seek(j1 * 520);
				int k = 0;
				int i2 = i1 - k1;
				if(i2 > 512)
					i2 = 512;
				int j2;
				for(; k < i2 + 8; k += j2) {
					j2 = dataFile.read(buffer, k, (i2 + 8) - k);
					if(j2 == -1)
						return null;
				}
				int k2 = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);
				int l2 = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
				int i3 = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8) + (buffer[6] & 0xff);
				int j3 = buffer[7] & 0xff;
				if(k2 != i || l2 != l1 || j3 != type.getIndex() + 1)
					return null;
				if(i3 < 0 || (long)i3 > dataFile.length() / 520L)
					return null;
				for(int k3 = 0; k3 < i2; k3++)
					abyte0[k1++] = buffer[k3 + 8];

				j1 = i3;
			}

			return abyte0;
		} catch(IOException _ex) {
			return null;
		}
	}

	/**
	 * Attempts to unpack data from the main cache file.
	 * The index and data file formats are as follows:
	 * 
	 * Index:
	 * - 3 bytes: file size
	 * - 3 bytes: initial data block id
	 * 
	 * Data:
	 * - 2 bytes: next file index id
	 * - 2 bytes: current data block file id
	 * - 3 bytes: next data block id
	 * - 1 byte: next file type
	 * - 512 bytes: data block
	 * 
	 * @param index The index file we should access.
	 * @return byte[] The data we're trying to unpack.
	 */
	public synchronized byte[] unpack(Index index) {
		try {
			if(index.getInitialDataBlockId() < 0 || (long)index.getInitialDataBlockId() > dataFile.length() / 520L) {
				System.out.println("Failed first check.");
				return null;
			}
			
			byte unpackedDataSet[] = new byte[index.getLength()];
			int bytesUnpacked = 0;
			for(int currentDataBlockId = 0; bytesUnpacked < index.getLength(); currentDataBlockId++) {
				if(index.getInitialDataBlockId() == 0) {
					System.out.println("Failed second check.");
					return null;
				}
				dataFile.seek(index.getInitialDataBlockId() * 520);
				int dataBlockLength = index.getLength() - bytesUnpacked;
				if(dataBlockLength > 512)
					dataBlockLength = 512;
				
				int bytesRead = dataFile.read(buffer, 0, 8);
				
				if (bytesRead != 8) {
					System.out.println("Failed third check.");
					return null;
				}
				
				int nextDataBlockFileIndex = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);
				int fileDataBlockId = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
				int nextDataBlockId = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8) + (buffer[6] & 0xff);
				int nextFileType = buffer[7] & 0xff;
				
				if(nextDataBlockFileIndex != index.getOffset()) {
					System.out.println("Failed fourth check.");
					return null;
				}
				
				if (fileDataBlockId != currentDataBlockId) {
					System.out.println("Failed fifth check.");
					return null;
				}
				
				if (nextFileType != (type.getIndex() + 1)) {
					System.out.println("Failed sixth check.");
					return null;
				}
				
				if(nextDataBlockId < 0 || (long)nextDataBlockId > dataFile.length() / 520L) {
					System.out.println("Failed seventh check.");
					return null;
				}
				
				for(int k3 = 0; k3 < dataBlockLength; k3++)
					unpackedDataSet[bytesUnpacked++] = buffer[k3 + 8];

				index.setInitialDataBlockId(nextDataBlockId);
			}

			return unpackedDataSet;
		} catch(IOException _ex) {
			_ex.printStackTrace();
			return null;
		}
	}

	public synchronized boolean repack(int length, byte[] data, int index) {
		if(!repack(true, index, length, data))
			if(repack(false, index, length, data))
				return true;
		return false;
	}

	/**
	 * Attempts to repack data to the main cache file.
	 * The index and data file formats are as follows:
	 * 
	 * Index:
	 * - 3 bytes: file size
	 * - 3 bytes: initial data block id
	 * 
	 * Data:
	 * - 2 bytes: next file index id
	 * - 2 bytes: current data block file id
	 * - 3 bytes: next data block id
	 * - 1 byte: next file type
	 * - 512 bytes: data block
	 * 
	 * @param dataBlockExists
	 * @param offset The offset of the file we should access.
	 * @param length The length of the data we're trying to repack.
	 * @param data The data we're trying to repack.
	 * @return boolean Whether the repacking was successful.
	 */
	private synchronized boolean repack(boolean dataBlockExists, int offset, int length, byte[] data) {
		try {
			int initialDataBlockId = getInitialDataBlockId(dataBlockExists, offset);
			
			if (initialDataBlockId == 0)
				return false;
			
			Index index = new Index(length, initialDataBlockId, offset, type);
			
			writeToIndexFile(index);
			
			int bytesWritten = 0;
			for(int currentDataBlockId = 0; bytesWritten < length; currentDataBlockId++) {
				int nextDataBlockId = 0;
				if(dataBlockExists) {
					dataFile.seek(initialDataBlockId * 520);
					
					int totalBytesRead;
					int bytesRead;
					for(totalBytesRead = 0; totalBytesRead < 8; totalBytesRead += bytesRead)
						if((bytesRead = dataFile.read(buffer, totalBytesRead, 8 - totalBytesRead)) == -1)
							break;
					
					if(totalBytesRead == 8) {
						int nextDataBlockFileIndex = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);
						int fileDataBlockId = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
						nextDataBlockId = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8) + (buffer[6] & 0xff);
						int nextFileType = buffer[7] & 0xff;
						if(nextDataBlockFileIndex != offset || fileDataBlockId != currentDataBlockId || nextFileType != type.getIndex() + 1)
							return false;
						if(nextDataBlockId < 0 || (long)nextDataBlockId > dataFile.length() / 520L)
							return false;
					}
				}
				if(nextDataBlockId == 0) {
					dataBlockExists = false;
					nextDataBlockId = (int)((dataFile.length() + 519L) / 520L);
					if(nextDataBlockId == 0)
						nextDataBlockId++;
					if(nextDataBlockId == initialDataBlockId)
						nextDataBlockId++;
				}
				if(length - bytesWritten <= 512)
					nextDataBlockId = 0;
				
				int bytesToWrite = length - bytesWritten;
				
				if(bytesToWrite > 512)
					bytesToWrite = 512;
				
				writeToDataFile(offset, currentDataBlockId, nextDataBlockId, initialDataBlockId, data, bytesWritten, bytesToWrite);
				
				bytesWritten += bytesToWrite;
				
				initialDataBlockId = nextDataBlockId;
			}

			return true;
		} catch(IOException _ex) {
			return false;
		}
	}
	
	private void writeToIndexFile(Index index) throws IOException {
		buffer[0] = (byte)(index.getLength() >> 16);
		buffer[1] = (byte)(index.getLength() >> 8);
		buffer[2] = (byte)(index.getLength());
		buffer[3] = (byte)(index.getInitialDataBlockId() >> 16);
		buffer[4] = (byte)(index.getInitialDataBlockId() >> 8);
		buffer[5] = (byte)(index.getInitialDataBlockId());
		indexFile.seek(index.getOffset() * 6);
		indexFile.write(buffer, 0, 6);
	}
	
	private void writeToDataFile(int offset, int currentDataBlockId, int nextDataBlockId, int initialDataBlockId, byte[] data, int bytesWritten, int bytesToWrite) throws IOException {
		buffer[0] = (byte)(offset >> 8);
		buffer[1] = (byte)(offset);
		buffer[2] = (byte)(currentDataBlockId >> 8);
		buffer[3] = (byte)(currentDataBlockId);
		buffer[4] = (byte)(nextDataBlockId >> 16);
		buffer[5] = (byte)(nextDataBlockId >> 8);
		buffer[6] = (byte)(nextDataBlockId);
		buffer[7] = (byte)(type.getIndex() + 1);
		dataFile.seek(initialDataBlockId * 520);
		dataFile.write(buffer, 0, 8);
		dataFile.write(data, bytesWritten, bytesToWrite);
	}
	
	public DataBlock getDataBlock(int dataBlockId) throws IOException {
		dataFile.seek(dataBlockId * 520);
		
		DataBlock dataBlock = null;
		
		if(dataFile.read(buffer, 0, 8) != 8)
			return null;
		
		dataFile.read(buffer, 8, 512);
		byte[] data = Arrays.copyOfRange(buffer, 8, buffer.length);
		
		int dataSize = 0;
		
		for (byte b : data)
			if (b != 0)
				dataSize++;
		
		int nextDataBlockFileIndex = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);
		int fileDataBlockId = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
		int nextDataBlockId = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8) + (buffer[6] & 0xff);
		int nextFileType = buffer[7] & 0xff;
		
		dataBlock = new DataBlock(dataBlockId, nextDataBlockFileIndex, fileDataBlockId, nextDataBlockId, nextFileType, data, dataSize);
		
		return dataBlock;
	}
	
	private int getInitialDataBlockId(boolean dataBlockExists, int index) throws IOException {
		int initialDataBlockId;
		
		if(dataBlockExists) {
			indexFile.seek(index * 6);
			
			int bytesRead;
			for(int i = 0; i < 6; i += bytesRead)
				if((bytesRead = indexFile.read(buffer, i, 6 - i)) == -1)
					return 0;
			
			initialDataBlockId = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);
			
			if(initialDataBlockId <= 0 || (long)initialDataBlockId > dataFile.length() / 520L)
				return 0;
		} else
			if((initialDataBlockId = (int)((dataFile.length() + 519L) / 520L)) == 0)
				initialDataBlockId = 1;
		
		return initialDataBlockId;
	}
	
	public ArrayList<Index> getIndices() {
		ArrayList<Index> indices = new ArrayList<>();

		int i = 1;
		
		try {
			while (i * 6 < indexFile.length()) {
				Index index = getIndex(i);
				
				if (index != null) {
					indices.add(index);
				}
				
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return indices;
	}
	
	public Index getIndex(int offset) throws IOException {
		indexFile.seek(offset * 6);

		if (indexFile.read(buffer, 0, 6) != 6)
			return null;

		int dataSetLength = ((buffer[0] & 0xff) << 16) + ((buffer[1] & 0xff) << 8) + (buffer[2] & 0xff);
		int initialDataBlockId = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);

		return new Index(dataSetLength, initialDataBlockId, offset, type);
	}
	
	public IndexType getIndexType() {
		return type;
	}
	
}
