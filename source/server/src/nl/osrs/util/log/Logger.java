package nl.osrs.util.log;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 
 * @author nick
 */
public class Logger extends PrintStream {

	private DateFormat dateFormat = new SimpleDateFormat();
	private Date cachedDate = new Date();

	public Logger(PrintStream out) {
		super(out);
	}

	@Override
	public void print(String str) {
		if (str.startsWith("debug:"))
			super.print("[" + getPrefix() + "] DEBUG: " + str.substring(6));
		else
			super.print("[" + getPrefix() + "]: " + str);
	}

	private String getPrefix() {
		return dateFormat.format(cachedDate);
	}
}
