package potters.pof.web;

import potters.pof.Logger;
import potters.pof.stats.chart.ChartBuilder;
import potters.pof.stats.visuals.LocationCheck;
import potters.pof.stats.visuals.QueryType;
import potters.pof.stats.visuals.UniqueWordFrequencyCheck;

public class HtmlResults implements Runnable{

	private TwitterSniffer twitter_sniffer;
	private Thread thread;
	private boolean running = true;
	private String html_last_day;
	private String html_last_day_noretweets;
	
	public HtmlResults(TwitterSniffer twitter_sniffer)
	{
		this.twitter_sniffer = twitter_sniffer;
	}
	
	public void Start()
	{
		Logger.Log("Launching HtmlResults handler");
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() 
	{
		while(running)
		{
			Logger.Log("Updating LAST_DAY HTML page..");
			html_last_day = BuildHTML(QueryType.LAST_DAY, "en", 20, true);
			html_last_day_noretweets = BuildHTML(QueryType.LAST_DAY, "en", 20, false);
			Logger.Log("Built LAST_DAY");
			
			try 
			{
				Thread.sleep(60000 * 2);
			} 
			catch (InterruptedException e) 
			{
				Logger.Log("Caught error! '" + e.getLocalizedMessage() + "'");
				for(StackTraceElement ste : e.getStackTrace())
					Logger.Log("\t" + ste.toString());
			}
		}
	}	

	public String GetHTMLAllTime()
	{
		return "Unavaliable at the momemnt";
	}
	
	public String GetHTMLLastDay(boolean include_retweets)
	{
		while(html_last_day == null || html_last_day_noretweets == null)
		{
			try 
			{
				Thread.sleep(1000);
			} 
			catch (InterruptedException e) 
			{
				Logger.Log("Caught error! '" + e.getLocalizedMessage() + "'");
				for(StackTraceElement ste : e.getStackTrace())
					Logger.Log("\t" + ste.toString());
			}
		}
		
		return include_retweets ? html_last_day : html_last_day_noretweets;
	}
	
	public void Stop()
	{
		try 
		{
			running = false;
			thread.join();
		} 
		catch (InterruptedException e) 
		{
			Logger.Log("Caught error! '" + e.getLocalizedMessage() + "'");
			for(StackTraceElement ste : e.getStackTrace())
				Logger.Log("\t" + ste.toString());
		}
	}

	
	private String BuildHTML(QueryType query_type, String lang, int word_count, boolean include_retweets)
	{
		UniqueWordFrequencyCheck word_check = new UniqueWordFrequencyCheck(lang, word_count);		
		LocationCheck geo_check = new LocationCheck();
		//USLocationCheck us_geo_check = new USLocationCheck();

		String required_headers = "language";
		String required_values = "en";
		
		if(!include_retweets)
		{
			required_headers += ",is_retweet";
			required_values += ",false";
		}
		
		switch(query_type)
		{
			case ALL_TIME:
				twitter_sniffer.happy_stat_manager.QueryAllTime(
						required_headers, required_values,
						word_check, geo_check
				);
				twitter_sniffer.sad_stat_manager.QueryAllTime(
						required_headers, required_values,
						word_check, geo_check
				);
				break;
				
			case LAST_DAY:
				twitter_sniffer.happy_stat_manager.QueryPastDay(
						required_headers, required_values,
						word_check, geo_check
				);
				twitter_sniffer.sad_stat_manager.QueryPastDay(
						required_headers, required_values,
						word_check, geo_check
				);
				break;	
		}
			
		
		String html = ChartBuilder.BuildHTML(
				word_check.GetHappyChart(),
				word_check.GetSadChart(),
				geo_check.GetChart()
		);
		
		return html;
	}
}
