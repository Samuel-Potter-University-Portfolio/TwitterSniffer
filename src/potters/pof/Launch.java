package potters.pof;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.*;

import potters.pof.stats.visuals.QueryType;
import potters.pof.web.HtmlResults;
import potters.pof.web.Server;
import potters.pof.web.TwitterSniffer;

public class Launch {

	private static TwitterSniffer twitter_sniffer;
	private static HtmlResults html_stats;
	
	static class DebugHandler implements HttpHandler
	{

		@Override
		public void handle(HttpExchange http_in) throws IOException 
		{
			String response = "Closing..";
			http_in.sendResponseHeaders(200, response.length());
            OutputStream os = http_in.getResponseBody();
            os.write(response.getBytes());
            os.close();
            
            System.exit(0);
		}
	}
	
	static class LogHandler implements HttpHandler
	{

		@Override
		public void handle(HttpExchange http_in) throws IOException 
		{
			String response = Logger.GetLogString();
			http_in.sendResponseHeaders(200, response.length());
            OutputStream os = http_in.getResponseBody();
            os.write(response.getBytes());
            os.close();
		}
	}
	
	static class ChartHandler implements HttpHandler
	{		
		
		@Override
		public void handle(HttpExchange http_in) throws IOException 
		{
			boolean retweets = true;
			QueryType query_type = QueryType.LAST_DAY;
			
			String raw_params = http_in.getRequestURI().getQuery();
			
			if(raw_params != null)
			{
				for(String param: raw_params.split("&"))
				{					
					if(param == null | param.isEmpty())
						continue;
					
					String[] parts = param.split("=");
					
					if(parts.length > 2)
						continue;

					final String key = parts[0];
					final String value = parts[1];
				
					Logger.Log(key + ":" + value);
					
					if(key.equalsIgnoreCase("query"))
					{
						if(value.equalsIgnoreCase("all_time"))
							query_type = QueryType.ALL_TIME;
					}
										
					if(key.equalsIgnoreCase("retweets"))
					{
						try
						{
							retweets = Boolean.parseBoolean(value);
						}catch(Exception e)
						{
							
						}
					}
				}
			}			
			
			String response = query_type == QueryType.ALL_TIME ? html_stats.GetHTMLAllTime() : html_stats.GetHTMLLastDay(retweets);
			http_in.sendResponseHeaders(200, response.length());
            OutputStream os = http_in.getResponseBody();
            os.write(response.getBytes());
            os.close();
		}
	}
	
	public static int FindInt(String opt, String[] args, int default_value)
	{
		for(String arg: args)
		{
			if(arg.startsWith("-" + opt))
			{
				String[] parts = arg.split("=");
				
				return Integer.parseInt(parts[1]);
			}
		}
		return default_value;
	}
	
	
	public static void main(String[] args)
	{		
		Logger.Log("Passed args:");
		for(String arg: args)
			Logger.Log("\t'" + arg + "'");
		
		Server server = null;
		try 
		{
			int port = FindInt("port", args, 80);
			Logger.Log("Passed port " + port);
			
			server = new Server(port);
			server.AddEndpoint("/plzerinocloserino", new DebugHandler());
			server.AddEndpoint("/log", new LogHandler());
			server.AddEndpoint("/charts", new ChartHandler());
			server.AddEndpoint("/charts/test", new LogHandler());
			server.Launch();
		} 
		catch (Exception e)
		{
			Logger.Log("Caught error! '" + e.getLocalizedMessage() + "'");
			for(StackTraceElement ste : e.getStackTrace())
				Logger.Log("\t" + ste.toString());
			
            System.exit(0);
		}
		
		try 
		{
			twitter_sniffer = new TwitterSniffer();
			html_stats = new HtmlResults(twitter_sniffer);
			
			html_stats.Start();
			twitter_sniffer.Run();
			
			if(server != null)
				server.Stop();
			html_stats.Stop();
		} 
		catch (Exception e)
		{
			Logger.Log("Caught error! '" + e.getLocalizedMessage() + "'");
			for(StackTraceElement ste : e.getStackTrace())
				Logger.Log("\t" + ste.toString());

            System.exit(0);
		}
	}	
}
