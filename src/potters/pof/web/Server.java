package potters.pof.web;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import potters.pof.Logger;


public class Server implements Runnable{

	private HttpServer web_server;
	private Thread thread;
	private final int port;
	
	public Server(int port) throws IOException
	{
		this.port = port;
		web_server = HttpServer.create(new InetSocketAddress(port), 0);
	}
	
	public void AddEndpoint(String endpoint, HttpHandler handler)
	{
		web_server.createContext(endpoint, handler);
	}
	
	public void Launch()
	{
		Logger.Log("Launching Web Server on port " + port);
		thread = new Thread(this);
		thread.start();
	}
	
	public void run()
	{
		web_server.setExecutor(null);
		web_server.start();
	}
	
	public void Stop()
	{
		try 
		{
			web_server.stop(0);
			thread.join();
		} 
		catch (InterruptedException e) 
		{
			Logger.Log("Caught error! '" + e.getLocalizedMessage() + "'");
			for(StackTraceElement ste : e.getStackTrace())
				Logger.Log("\t" + ste.toString());
		}
	}
}
