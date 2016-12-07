package potters.pof.stats;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import potters.pof.Logger;
import winterwell.jtwitter.Place;
import winterwell.jtwitter.Status;

public class StatDB extends Ticker
{
	private final String TABLE_NAME;
	private final String CSV_HEADER = "tweet_id,text,is_retweet,is_sensitive,is_quoted,created_at,language,"
			+ "user_name,user_screen_name,user_id,user_location,user_followers,user_verified,user_timezone,user_website,"
			+ "place_country_code,place_country_name,place_longitude,place_latitude,place_type";
		
	public StatDB(String table_name)
	{
		this.TABLE_NAME = table_name;		
		Load();
	}

	private void Load()
	{
		File file = new File(TABLE_NAME);
		
		if(!file.exists())
		{
			Logger.Log("\tCreating CSV for " + TABLE_NAME);
			
			try 
			{
				final String content = CSV_HEADER + "\n";
				
				FileOutputStream out = new FileOutputStream(file);
				out.write(content.getBytes());
				out.close();			
				
				Logger.Log("\tSuccessful.");
			} 
			catch (IOException e) 
			{
				Logger.Log("Caught error! '" + e.getLocalizedMessage() + "'");
				for(StackTraceElement ste : e.getStackTrace())
					Logger.Log("\t" + ste.toString());
			}
		}
		else
		{
			Logger.Log("\tLoading existing CSV for " + TABLE_NAME);
		}
	}
	
	private String Safe(String s)
	{
		if(s == null)
			return "";

		if(s.startsWith("rt "))
			s.replaceAll("rt ", "");
		
		return "\"" + s.replaceAll(",", "").replaceAll("\"", "'").replaceAll("\n", "'").replaceAll("\r", "'").replaceAll("\t", "'").toLowerCase() + "\"";
	}
		
	public void AddTweet(Status tweet)
	{
		/*
			tweet_id, text, is_retweet, is_sensitive, is_quoted, created_at,language,
			user_name, user_screen_name, user_id, user_location, user_follower, user_verified, user_timezone, user_website,
			place_country_code, place_country_name, place_longitude, place_latitude, place_type
		 */
		
		try
		{
			String entry = "";
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			//Tweet
			entry += tweet.getId().toString() + ",";
			entry += Safe(tweet.getDisplayText()) + ",";
			entry += tweet.isRetweet() + ",";
			entry += tweet.isSensitive() + ",";
			entry += tweet.isQuotedStatus() + ",";
			entry += format.format(tweet.createdAt) + ",";
			entry += Safe(tweet.getLang()) + ",";
	
			//User
			entry += Safe(tweet.user.name) + ",";
			entry += Safe(tweet.user.screenName) + ",";
			entry += tweet.user.id + ",";
			entry += Safe(tweet.user.location) + ",";
			entry += tweet.user.followersCount + ",";
			entry += tweet.user.isVerified() + ",";
			entry += Safe(tweet.user.getTimezone()) + ",";
			entry += tweet.user.website + ",";
			
			//Place
			if(tweet.getPlace() != null)
			{
					Place p = tweet.getPlace();
	
					entry += p.getCountryCode() + ",";
					entry += p.getCountryName() + ",";
					entry += p.getCentroid().getLongitude() + ",";
					entry += p.getCentroid().getLatitude() + ",";
					entry += p.getType() + "";
			}
			else
			{
				entry += ",";
				entry += ",";
				entry += ",";
				entry += ",";
				entry += "";
			}
			
			entry = entry.replace("\n", "").replaceAll("null", "");
			entry += "\n";
							
			FileOutputStream out = new FileOutputStream(TABLE_NAME, true);
			out.write(entry.getBytes());
			out.close();		
		}
		catch (Exception e) 
		{
			Logger.Log("Caught error! '" + e.getLocalizedMessage() + "'");
			for(StackTraceElement ste : e.getStackTrace())
				Logger.Log("\t" + ste.toString());
		}
	}

	@Override
	protected void InternalTick(final long current_epoch)
	{
	}
}
