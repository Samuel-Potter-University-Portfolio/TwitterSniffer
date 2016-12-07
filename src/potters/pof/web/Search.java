package potters.pof.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import potters.pof.Logger;

@Deprecated
public class Search 
{
	private HttpClient http_client;
	private OAuthConsumer oauth_consumer;
	private String oauth_key = "rnZyhVbcnP0Qw2HDaWOmvvFJ4";
	private String oauth_secret = "uBewlFsmxJ04wnFgT4RKGG3URVVsqOmFA2EL3a6xtZuYDUw5PQ";
	private String last_max_id = "";
	
	public Search()
	{
		http_client = HttpClientBuilder.create().build();
		oauth_consumer = new DefaultOAuthConsumer(oauth_key, oauth_secret);
	}
	
	public ArrayList<String> Get(String keyword)
	{
		ArrayList<String> results = new ArrayList<String>();
		
		String url = "https://api.twitter.com/1.1/search/tweets.json";
		String url_params = "q=" + keyword + "&count=100" + (last_max_id.isEmpty() ? "" : "&since_id=" + last_max_id); 
		
		HttpGet request = new HttpGet(url + "?" + url_params);
		try 
		{
			oauth_consumer.sign(request);

			HttpResponse response = http_client.execute(request);
			Logger.Log(response.getStatusLine());
		} 
		catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException | IOException e) 
		{
			
			e.printStackTrace();
		}
		
		
		/*	
		try 
		{
			Response response = client.newCall(request).execute();
			
			if(response.code() != 200)
			{
				System.err.println("Error in search for '" + keyword + "' (Response:" + response.code() + ")");
				System.err.println(response.body().string());
			}
			else
			{
				System.err.println("Cool");	
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		*/
		
		return results;
	}
	
}
