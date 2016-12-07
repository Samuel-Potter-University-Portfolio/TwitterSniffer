package potters.pof;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {
	
	private static String[] log_buffer = new String[128];
	private static int index = 0;
	
	private static int NextIndex(int index)
	{
		index++;
		if(index >= log_buffer.length)
			return 0;
		else
			return index;
	}
	
	private static String GetTimeStamp()
	{
		DateFormat format = new SimpleDateFormat("[HH:mm:ss]");
		String raw =  format.format(Calendar.getInstance().getTime());
		return raw;
	}
	
	public static void Log(Object obj)
	{
		String entry = GetTimeStamp() + ": " + obj;
		System.out.println(entry);
		log_buffer[index] = entry;
		index = NextIndex(index);
	}
	
	public static String GetLogString()
	{
		int actual_index = index;
		String output = "";
		
		for(int i = 0; i<log_buffer.length; i++)
		{
			if(log_buffer[actual_index] != null)
				output += log_buffer[actual_index] + "\n";
			actual_index = NextIndex(actual_index);
		}
		
		return output;
	}
}
