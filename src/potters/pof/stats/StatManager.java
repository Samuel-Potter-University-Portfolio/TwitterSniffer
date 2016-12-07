package potters.pof.stats;

import java.io.File;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import potters.pof.Logger;
import potters.pof.stats.visuals.DataTask;
import potters.pof.web.struct.JsonHandler;
import potters.pof.web.struct.CsvHandler;
import winterwell.json.JSONObject;
import winterwell.jtwitter.Status;

public class StatManager extends Ticker
{	
	
	private final String DB_ROOT;
	private final String META_NAME = "meta";
	private JSONObject meta_table;
	
	private StatDB split_db;
	private ArrayList<Status> statuses = new ArrayList<Status>();
	private String last_time_stamp;
	private BigInteger largest_found_current = BigInteger.ZERO;
	private BigInteger largest_found_total = BigInteger.ZERO;

	private static String GetTimeStamp()
	{
		DateFormat format = new SimpleDateFormat("yy-MM-dd-HH");
		String raw =  format.format(Calendar.getInstance().getTime());
		return raw;
	}
	
	public static String GetTimeStamp(Date date)
	{
		DateFormat format = new SimpleDateFormat("yy-MM-dd-HH");
		String raw =  format.format(date);
		return raw;
	}
	
	public static Date ParseTimeStamp(String date) throws ParseException
	{
		DateFormat format = new SimpleDateFormat("yy-MM-dd-HH");
		return format.parse(date);
	}
	
	public StatManager(final String root)
	{
		Logger.Log("Initializing " + root + " StatManager..");
		this.DB_ROOT = "PoF-stat/" + root + "/";
		
		File file = new File(DB_ROOT);
		Logger.Log("\tMaking dirs? " + DB_ROOT + " " + file.mkdirs());
		LoadMetaTable();
	}
	
	private void LoadMetaTable()
	{
		meta_table = JsonHandler.ReadObject(DB_ROOT + META_NAME + ".json");
		String time_stamp = GetTimeStamp();
		
		if(meta_table == null)
		{
			Logger.Log("Meta table not found; assuming new");
			meta_table = new JSONObject();
			meta_table.put("start", time_stamp);
		}
		else
		{
			largest_found_total = new BigInteger(meta_table.getString("largest_id"));
			largest_found_current = largest_found_total;
			Logger.Log("Starting with largest_id:" + largest_found_total);
		}
		
		split_db = new StatDB(DB_ROOT + time_stamp + ".csv");
		
		if(!meta_table.has(time_stamp))
			meta_table.put(time_stamp, 0);
		
		last_time_stamp = time_stamp;
	}
	
	
	public boolean AddTweet(Status tweet)
	{				
		if(tweet.id.compareTo(largest_found_current) == 1)
			largest_found_current = tweet.id;
		
		if(tweet.id.compareTo(largest_found_total) == 1)
		{
			statuses.add(tweet);
			return true;
		}
		return false;
	}

	@Override
	public void InternalTick(final long current_epoch)
	{
		Logger.Log("\tWriting meta_table result:" +
				JsonHandler.Write(meta_table, DB_ROOT + META_NAME + ".json")
		);
	}
	
	@Override
	public void Tick(final long current_epoch)
	{
		String time_stamp = GetTimeStamp();
		
		if(!last_time_stamp.equals(time_stamp))
		{
			Logger.Log("New time stamp:" + time_stamp);
			meta_table.put(time_stamp, 0);
			split_db = new StatDB(DB_ROOT + time_stamp + ".csv");
			last_time_stamp = time_stamp;
		}
		
		
		for(Status tweet: statuses)
		{
			split_db.AddTweet(tweet);
		}
		
		if(largest_found_current.compareTo(largest_found_total) == 1)
		{
			largest_found_total = largest_found_current;
			meta_table.put("largest_id", largest_found_total.toString());
		}

		split_db.Update();
		
		Logger.Log("Added " + statuses.size() + " tweets to DB (" + DB_ROOT + ")");
		statuses.clear();
	}

	@SuppressWarnings("deprecation")
	public void QueryTimeframe(Date from, Date until, String required_headers, String required_values, DataTask... tasks)
	{
		Date current = from;
		
		while(current.before(until) || current.equals(until))
		{
			String time_stamp = GetTimeStamp(current);
			
			if(meta_table.has(time_stamp))
				CsvHandler.Proccess(DB_ROOT + time_stamp + ".csv", required_headers, required_values, tasks);
			//else
			//	Logger.Log("\tSkipping: " + time_stamp);
			
			current.setHours(current.getHours() + 1);
		}
		
		for(DataTask task: tasks)
			task.OnComplete();	
	}
	
	public void QueryAllTime(String required_headers, String required_values, DataTask... tasks)
	{
		Date from = null;
		Date until = null;
		try 
		{
			from = ParseTimeStamp(meta_table.getString("start"));
			until = ParseTimeStamp(GetTimeStamp());
		} 
		catch (ParseException e) 
		{
			Logger.Log("Caught error! '" + e.getLocalizedMessage() + "'");
			for(StackTraceElement ste : e.getStackTrace())
				Logger.Log("\t" + ste.toString());
		}
		
		if(from == null || until == null)
		{
			Logger.Log("Unable to set dates for QueryAllTime");
		}
		
		QueryTimeframe(from, until, required_headers, required_values, tasks);
	}
	
	@SuppressWarnings("deprecation")
	public void QueryPastDay(String required_headers, String required_values, DataTask... tasks)
	{
		Date from = null;
		Date until = null;
		try 
		{
			from = ParseTimeStamp(GetTimeStamp());
			until = ParseTimeStamp(GetTimeStamp());
			from.setHours(from.getHours() - 23);
			until.setHours(until.getHours() + 1);
		} 
		catch (ParseException e) 
		{
			Logger.Log("Caught error! '" + e.getLocalizedMessage() + "'");
			for(StackTraceElement ste : e.getStackTrace())
				Logger.Log("\t" + ste.toString());
		}
		
		if(from == null || until == null)
		{
			Logger.Log("Unable to set dates for QueryAllTime");
		}
		
		QueryTimeframe(from, until, required_headers, required_values, tasks);
	}
}

