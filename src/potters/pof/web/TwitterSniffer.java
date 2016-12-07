package potters.pof.web;

import java.util.List;

import potters.pof.Logger;
import potters.pof.stats.StatManager;
import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Status;
import winterwell.jtwitter.Twitter;

public class TwitterSniffer 
{
	private Twitter twitter;
	public StatManager happy_stat_manager = new StatManager("happy");
	public StatManager sad_stat_manager = new StatManager("sad");

	private String oauth_key = "";
	private String oauth_secret = "";

	private int current_count_accepted = 0;
	private int current_count_total = 0;
	private int sleep_time = 15;
	private int gc_counter = 0;
	
	public TwitterSniffer()
	{
		OAuthSignpostClient oauth_client = new OAuthSignpostClient(oauth_key, oauth_secret, "oob");
		
		twitter = new Twitter(" ", oauth_client);
		twitter.setSearchResultType(Twitter.SEARCH_RECENT);		
	}
	
	public void Search(String word, StatManager stat_manager)
	{
		List<Status> tweets = twitter.search(word);
		Logger.Log("\tSearching for '" + word + "'..");
		
		current_count_total += 100;
		for(Status tweet: tweets)
		{						
			if(stat_manager.AddTweet(tweet))	
				current_count_accepted++;
		}
		
	}
	
	public void Run()
	{		
		while(true)
		{
			try
			{
				current_count_accepted = 0;
				current_count_total = 0;

				Logger.Log("");	
				Logger.Log("");	
				Logger.Log("=============================================");	
				Logger.Log("Updating..");		
				Search(":(", sad_stat_manager);
				Search(":)", happy_stat_manager);
				
				Logger.Log("Finshed all searches.");	
				Logger.Log("Processed " + current_count_accepted + " new out " + current_count_total + " total.");	
				
				happy_stat_manager.Update();	
				sad_stat_manager.Update();
				
				float dif = 1;
				if(current_count_total != 0)
					dif = (float)current_count_accepted/(float)current_count_total;
							
				if(dif < 0.25f)
					sleep_time += 1;
				else if(dif > 0.75f)
					sleep_time -= 1;
							
				if(sleep_time < 6)
					sleep_time = 6;
				
				
				if(gc_counter++ >= 20)
				{
					Logger.Log("Forcing System.gc()");
					System.gc();
					gc_counter = 0;
				}
				
				Logger.Log("Sleeping for " + sleep_time + "..");
			}
			catch(Exception e)
			{
				Logger.Log("Caught error! '" + e.getLocalizedMessage() + "'");
				for(StackTraceElement ste : e.getStackTrace())
					Logger.Log("\t" + ste.toString());
			}	
			
			try 
			{
				Thread.sleep(sleep_time * 1000);
			} 
			catch (InterruptedException e) 
			{
				Logger.Log("Caught error! '" + e.getLocalizedMessage() + "'");
				for(StackTraceElement ste : e.getStackTrace())
					Logger.Log("\t" + ste.toString());
			}
		}
	}
	
}
