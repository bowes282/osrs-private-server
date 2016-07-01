package nl.osrs;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class GetAllScriptFiles {
	
	public static void main(String[] args) {
		Collection<File> scripts = getAllFilesThatMatchFilenameExtension("../scripts", "*.js");
		System.out.println(scripts.size());
	}
	
	private static Collection<File> getAllFilesThatMatchFilenameExtension(String directoryName, String extension)
	{
		File directory = new File(directoryName);
		
		return FileUtils.listFiles(directory, new WildcardFileFilter(extension), DirectoryFileFilter.DIRECTORY);
	}
	
}
