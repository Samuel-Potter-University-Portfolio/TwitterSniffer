package potters.pof.web.struct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import potters.pof.Logger;
import winterwell.json.JSONArray;
import winterwell.json.JSONObject;

public class JsonHandler 
{	
	public static boolean Write(JSONObject json_obj, final String file_path)
	{
		try 
		{
			FileOutputStream out = new FileOutputStream(file_path);
			out.write(json_obj.toString().getBytes());
			out.close();				
			Logger.Log("\tWritten json object to '" + file_path + "'");
			return true;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return false;
	}

	public static boolean Write(JSONArray json_arr, final String file_path)
	{
		try 
		{
			FileOutputStream out = new FileOutputStream(file_path);
			out.write(json_arr.toString().getBytes());
			out.close();				
			Logger.Log("\tWritten json array to '" + file_path + "'");
			return true;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static JSONObject ReadObject(final String file_path)
	{
		try
		{
			File file = new File(file_path);
			
			if(file.exists())
			{
				String raw_json = "";
				BufferedReader reader = new BufferedReader(new FileReader(file));
								
				String line;
				while((line = reader.readLine()) != null)
					raw_json += line;
	
				if(reader != null)
					reader.close();
				
				Logger.Log("\tSuccessfully read json object '" + file_path + "'");
				JSONObject data = new JSONObject(raw_json);	
				
				return data;
			}
			else
			{
				System.err.println("\tError in reading '" + file_path + "'");	
				return null;
			}
		}
		catch (IOException e)
		{
			Logger.Log("Caught error! '" + e.getLocalizedMessage() + "'");
			for(StackTraceElement ste : e.getStackTrace())
				Logger.Log("\t" + ste.toString());
		}
		
		return null;
	}
	
	public static JSONArray ReadArray(final String file_path)
	{
		try
		{
			File file = new File(file_path);
			
			if(file.exists())
			{
				String raw_json = "";
				BufferedReader reader = new BufferedReader(new FileReader(file));
								
				String line;
				while((line = reader.readLine()) != null)
					raw_json += line;
	
				if(reader != null)
					reader.close();
				
				if(raw_json == "")
					return null;
				
				Logger.Log("\tSuccessfully read json array '" + file_path + "'");
				JSONArray data = new JSONArray(raw_json);	
				
				return data;
			}
			else
			{
				System.err.println("\tError in reading '" + file_path + "'");	
				return null;
			}
		}
		catch (IOException e)
		{
			Logger.Log("Caught error! '" + e.getLocalizedMessage() + "'");
			for(StackTraceElement ste : e.getStackTrace())
				Logger.Log("\t" + ste.toString());
		}
		
		return null;
	}
}
