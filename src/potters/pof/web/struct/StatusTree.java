package potters.pof.web.struct;

import potters.pof.stats.Data;
import winterwell.json.JSONArray;
import winterwell.json.JSONObject;
import winterwell.jtwitter.Status;

@Deprecated
public class StatusTree 
{
	private String key;
	private StatusTree lhs_child = null;
	private StatusTree rhs_child = null;

	private int count = 0;
	private int fav_count = 0;
	private int rt_count = 0;

	public StatusTree(String key, Status tweet)
	{
		this.key = key;
		Update(tweet);
	}
	
	private StatusTree(String key, int count, int rt_count, int fav_count)
	{
		this.key = key;
		this.count = count;
		this.rt_count = rt_count;
		this.fav_count = fav_count;
	}
		
	private void Update(Status tweet)
	{
		if(tweet.isRetweet())
			rt_count++;
		else if(tweet.isFavorite())
			fav_count++;
		else
			count++;
	}
	
	public void Clear()
	{
		if(lhs_child != null)
			lhs_child.Clear();
		
		if(rhs_child != null)
			rhs_child.Clear();
		
		lhs_child = null;
		rhs_child = null;
		key = "";
	}
		
	public int GetTotalCount()
	{
		return count + rt_count + fav_count;		
	}
	
	private void AddChild(String key, int count, int rt_count, int fav_count)
	{
		int compare = this.key.compareTo(key);
		
		if(compare == 0)//Equal
		{
			this.count = count;
			this.rt_count = rt_count;
			this.fav_count = fav_count;
		}
		else if (compare > 0) //Less than
		{
			if(lhs_child != null)
				lhs_child.AddChild(key, count, rt_count, fav_count);
			else
				lhs_child = new StatusTree(key, count, rt_count, fav_count);
		}
		else
		{
			if(rhs_child != null)
				rhs_child.AddChild(key, count, rt_count, fav_count);
			else
				rhs_child = new StatusTree(key, count, rt_count, fav_count);
		}
	}
	
	public void AddChild(String key, Status tweet)
	{
		int compare = this.key.compareTo(key);
		
		if(compare == 0)//Equal
			Update(tweet);
		else if (compare > 0) //Less than
		{
			if(lhs_child != null)
				lhs_child.AddChild(key, tweet);
			else
				lhs_child = new StatusTree(key, tweet);
		}
		else
		{
			if(rhs_child != null)
				rhs_child.AddChild(key, tweet);
			else
				rhs_child = new StatusTree(key, tweet);
		}
	}
	
	public StatusTree Get(String key)
	{
		int compare = this.key.compareTo(key);
		
		if(compare == 0)//Equal
			return this;
		else if (compare > 0) //Less than
		{
			if(lhs_child != null)
				return lhs_child.Get(key);
			else
				return null;
		}
		else
		{
			if(rhs_child != null)
				return rhs_child.Get(key);
			else
				return null;
		}
	}
	
	private Data PopulateInOrder(Data data, StatusMode status_mode, int min)
	{
		if(lhs_child != null)
			data = lhs_child.PopulateInOrder(data, status_mode, min);
		
		try 
		{
			int value = 0;
			
			switch(status_mode)
			{
			case TOTAL:
				value = GetTotalCount();
				break;
			case UNIQUE:
				value = count;
				break;
			case RETWEETS:
				value = rt_count;
				break;
			case FAVOURITES:
				value = fav_count;
				break;
			
			}
			
			if(value != 0 && (min == 0 || value > min))
				data.AddRow(key, value);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		if(rhs_child != null)
			data = rhs_child.PopulateInOrder(data, status_mode, min);
		
		return data;
	}
	
	private void PopulateJSONPreOrder(JSONArray array)
	{
		if(key != "")
		{
			JSONObject json = new JSONObject();
			json.put("key", key);
			json.put("count", count);
			json.put("rt_count", rt_count);
			json.put("fav_count", fav_count);
			array.put(json);
		}
		
		if(lhs_child != null)
			lhs_child.PopulateJSONPreOrder(array);
		
		if(rhs_child != null)
			rhs_child.PopulateJSONPreOrder(array);
	}
		
	public static Data GetAsData(StatusTree root_node, StatusMode status_mode, int minimum)
	{		
		Data data = new Data("Word","Count");
		data = root_node.PopulateInOrder(data, status_mode, minimum);
		return data;
	}
	
	public static JSONArray FormatAsJSONArray(StatusTree root_node)
	{
		JSONArray array = new JSONArray();
		root_node.PopulateJSONPreOrder(array);	
		return array;
	}
	
	public static StatusTree GenerateFromJSONArray(JSONArray array)
	{		
		StatusTree root_node = null;
		
		for(int i = 0; i <array.length();i++)
		{
			JSONObject obj = array.getJSONObject(i);
			
			
			String key = obj.optString("key", "");
			int count = obj.optInt("count", 0);
			int rt_count = obj.optInt("rt_count", 0);
			int fav_count = obj.optInt("fav_count", 0);
			
			if(key != "")
			{
				if(root_node == null)
					root_node = new StatusTree(key, count, rt_count, fav_count);
				else
					root_node.AddChild(key, count, rt_count, fav_count);
			}
		}
				
		return root_node;
	}
	
}
