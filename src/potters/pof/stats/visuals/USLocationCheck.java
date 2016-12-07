package potters.pof.stats.visuals;

import java.util.HashMap;
import java.util.Map;

import potters.pof.stats.Data;
import potters.pof.stats.chart.Chart;
import potters.pof.stats.chart.ChartType;

public class USLocationCheck implements DataTask
{
	private Data data;
	private HashMap<String,Integer> count;
	private HashMap<String,Integer> happy_count = new HashMap<String,Integer>();
	private HashMap<String,Integer> sad_count = new HashMap<String,Integer>();
	private boolean checking_happy = true;
	
	//To be read twice: happy first, sad second
	public USLocationCheck()
	{
		data = new Data("LONGITUDE", "LATITUDE", "Happiness %", "Total Tweets");
		count = happy_count;
	}
	
	@Override
	public void Execute(Map<String, String> values) 
	{
		if(!values.containsKey("place_country_code") || !values.get("place_country_code").equals("US")) 
			return;

		float longitude = Math.round(Float.parseFloat(values.get("place_longitude")));	
		float latitude = Math.round(Float.parseFloat(values.get("place_latitude")));	
		
		String place = longitude + "," + latitude;
		
		if(count.containsKey(place))
			count.put(place, count.get(place) + 1);
		else
			count.put(place, 1);
		
	}

	@Override
	public void OnComplete() 
	{
		if(checking_happy)
		{
			checking_happy = false;
			count = sad_count;
			return;
		}

		//Populate total_count
		HashMap<String, PlaceInfo> total_count = new HashMap<String, PlaceInfo>();

		for(String country: happy_count.keySet())
		{
			try 
			{
				PlaceInfo i = new PlaceInfo(happy_count.get(country), 0);
				total_count.put(country, i);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
		for(String place: sad_count.keySet())
		{
			try 
			{
				if(total_count.containsKey(place))
				{
					PlaceInfo i = total_count.get(place);
					i.sad = sad_count.get(place);
				}
				else
				{
					PlaceInfo i = new PlaceInfo(0, sad_count.get(place));
					total_count.put(place, i);
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
		//Add rows
		for(String place: total_count.keySet())
		{
			try 
			{
				PlaceInfo i = total_count.get(place);
				String[] loc = place.split(",");
				
				double longitude = Math.round(Float.parseFloat(loc[0]));	
				double latitude = Math.round(Float.parseFloat(loc[1]));
				
				data.AddRow(longitude, latitude, i.GetHappiness(), i.GetTotal());
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public Chart GetChart() 
	{
		Chart chart = new Chart(ChartType.GEOGRAPHICAL, data);
		//chart.geo_colour_axis = "";
		chart.geo_region = "US";
		return chart;
	}
}


class PlaceInfo
{
	int happy = 0;
	int sad = 0;
	
	public PlaceInfo(){}
	public PlaceInfo(int happy, int sad) 
	{
		this.happy = happy;
		this.sad = sad;
	}
	
	public int GetTotal()
	{
		return happy+sad;
	}
	
	public double GetHappiness()
	{
		return ((double)happy/(double)GetTotal())*100.0f;
	}
}