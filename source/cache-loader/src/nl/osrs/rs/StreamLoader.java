package nl.osrs.rs;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;

public final class StreamLoader {
	private static final byte[] gzipInputBuffer = new byte[0x71868];
	
	private static Decompressor[] decompressors;
	public static RandomAccessFile cache_dat = null;
	public static final RandomAccessFile[] cache_idx = new RandomAccessFile[5];
	
	public static final String CACHE_DIRECTORY = "../../data/client/";
	
	public static StreamLoader getConfig() {
		return forName(2, "config", "config", 0, 30);
	}

	public static StreamLoader forName(int i, String s, String s1, int j, int k) {
		if (decompressors == null)
			loadDecompressors();

		byte abyte0[] = null;
		try {
			if (decompressors[0] != null)
				abyte0 = decompressors[0].decompress(i);
		} catch (Exception _ex) {
		}
		
		if (abyte0 != null)
			return new StreamLoader(abyte0);
		
		return null;
	}
	
	public static Model getModel(int id) {
		byte[] abyte0 = null;
		
		if (decompressors[1] != null)
			abyte0 = decompressors[1].decompress(id);
		
		abyte0 = unzip(abyte0);
		
		Model.addModel(abyte0, id);
		
		return new Model(id);
	}
	
	private static void loadCache() {
		try {
			cache_dat = new RandomAccessFile(CACHE_DIRECTORY + "main_file_cache.dat", "rw");
			
			for (int j = 0; j < 5; j++)
				cache_idx[j] = new RandomAccessFile(CACHE_DIRECTORY + "main_file_cache.idx" + j, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void loadDecompressors() {
		decompressors = new Decompressor[5];
		
		if (cache_dat == null)
			loadCache();
		
		for (int i = 0; i < 5; i++)
			decompressors[i] = new Decompressor(cache_dat, cache_idx[i], i + 1);
	}
	
	public StreamLoader(byte abyte0[])
	{
		Stream stream = new Stream(abyte0);
		int i = stream.read3Bytes();
		int j = stream.read3Bytes();
		if(j != i)
		{
			byte abyte1[] = new byte[i];
			Class13.method225(abyte1, i, abyte0, j, 6);
			aByteArray726 = abyte1;
			stream = new Stream(aByteArray726);
			aBoolean732 = true;
		} else
		{
			aByteArray726 = abyte0;
			aBoolean732 = false;
		}
		dataSize = stream.readUnsignedWord();
		anIntArray728 = new int[dataSize];
		anIntArray729 = new int[dataSize];
		anIntArray730 = new int[dataSize];
		anIntArray731 = new int[dataSize];
		int k = stream.currentOffset + dataSize * 10;
		for(int l = 0; l < dataSize; l++)
		{
			anIntArray728[l] = stream.readDWord();
			anIntArray729[l] = stream.read3Bytes();
			anIntArray730[l] = stream.read3Bytes();
			anIntArray731[l] = k;
			k += anIntArray730[l];
		}
	}

	public byte[] getDataForName(String s)
	{
		byte abyte0[] = null; //was a parameter
		int i = 0;
		s = s.toUpperCase();
		for(int j = 0; j < s.length(); j++)
			i = (i * 61 + s.charAt(j)) - 32;

		for(int k = 0; k < dataSize; k++)
			if(anIntArray728[k] == i)
			{
				if(abyte0 == null)
					abyte0 = new byte[anIntArray729[k]];
				if(!aBoolean732)
				{
					Class13.method225(abyte0, anIntArray729[k], aByteArray726, anIntArray730[k], anIntArray731[k]);
				} else
				{
					System.arraycopy(aByteArray726, anIntArray731[k], abyte0, 0, anIntArray729[k]);
				}
				return abyte0;
			}

		return null;
	}

	private static byte[] unzip(byte[] bytes) {
		int i = 0;
		try {
			GZIPInputStream gzipinputstream = new GZIPInputStream(new ByteArrayInputStream(bytes));
			do {
				if (i == gzipInputBuffer.length)
					throw new RuntimeException("buffer overflow!");
				int k = gzipinputstream.read(gzipInputBuffer, i, gzipInputBuffer.length - i);
				if (k == -1)
					break;
				i += k;
			} while (true);
		} catch (IOException _ex) {
			_ex.printStackTrace();
			return null;
		}
		byte[] result = new byte[i];
		System.arraycopy(gzipInputBuffer, 0, result, 0, i);

		return result;
	}

	private final byte[] aByteArray726;
	private final int dataSize;
	private final int[] anIntArray728;
	private final int[] anIntArray729;
	private final int[] anIntArray730;
	private final int[] anIntArray731;
	private final boolean aBoolean732;
}
