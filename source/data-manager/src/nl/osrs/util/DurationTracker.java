package nl.osrs.util;

import java.util.Stack;

public class DurationTracker {
	private static Stack<Long> timeStamps = new Stack<>();

	public static final int SECOND_MS = 1000;
	public static final int MINUTE_MS = SECOND_MS * 60;
	public static final int HOUR_MS = MINUTE_MS * 60;
	public static final int DAY_MS = HOUR_MS * 24;
	
	public static void start() {
		timeStamps.push(System.currentTimeMillis());
	}
	
	public static String getDuration() {
		return getTimestamp(System.currentTimeMillis() - timeStamps.pop());
	}
	
	private static String getTimestamp(long time) {
		String timestamp = "";

		int days = (int) (time / DAY_MS);
		int hours = (int) ((time % DAY_MS) / HOUR_MS);
		int minutes = (int) ((time % HOUR_MS) / MINUTE_MS);
		int seconds = (int) ((time % MINUTE_MS) / SECOND_MS);
		int milliSeconds = (int) (time % SECOND_MS);
		
		if (days > 0)
			timestamp += days + "d ";
		
		if (hours > 0)
			timestamp += hours + "h ";
		
		if (minutes > 0)
			timestamp += minutes + "m ";
		
		if (seconds > 0)
			timestamp += seconds + "s ";
		
		if (milliSeconds > 0)
			timestamp += milliSeconds + "ms";
		
		return timestamp.substring(0, timestamp.length());
	}
}
