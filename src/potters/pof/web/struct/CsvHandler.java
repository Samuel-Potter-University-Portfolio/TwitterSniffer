package potters.pof.web.struct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import potters.pof.Logger;
import potters.pof.stats.visuals.DataTask;

public class CsvHandler {

	
	public static boolean Proccess(final String file_path, String required_headers, String required_values, DataTask... tasks)
	{
		Map<String,String> key_values = new HashMap<String,String>();
		
		try
		{
			File file = new File(file_path);
			
			if(file.exists())
			{
				BufferedReader reader = new BufferedReader(new FileReader(file));
								
				String input;
				boolean first_line = true;
				String[] headers = null;
				
				while((input = reader.readLine()) != null)
				{
					if(first_line)
					{
						first_line = false; 
						headers = input.split(",");
						continue;
					}
					
					String[] values = input.split(",");
					key_values.clear();
					
					for(int i = 0; i<values.length; i++)
						key_values.put(headers[i], values[i].replaceAll("\"", ""));
					
					try
					{
						String[] r_headers = required_headers.split(",");
						String[] r_values = required_values.split(",");
						boolean valid = true;
						
						for(int i = 0; i<r_headers.length; i++)
						{
							String h = r_headers[i].trim();
							String v = r_values[i].trim();
							
							if(!key_values.containsKey(h) || !key_values.get(h).equals(v))
							{
								valid = false;
								break;
							}
						}
									
						if(valid)
							for(DataTask task: tasks)
								task.Execute(key_values);		
					}
					catch(Exception e)
					{
						Logger.Log("Caught error! '" + e.getMessage() + "' for " + input);
						for(StackTraceElement ste : e.getStackTrace())
							Logger.Log("\t" + ste.toString());
					}
				}
	
				if(reader != null)
					reader.close();
				
				return true;
			}
			else
			{
				System.err.println("\tError in reading '" + file_path + "'");	
				return false;
			}
		}
		catch (IOException e)
		{
			Logger.Log("Caught error! '" + e.getLocalizedMessage() + "'");
			for(StackTraceElement ste : e.getStackTrace())
				Logger.Log("\t" + ste.toString());
		}
		
		return false;
	}
}
