package potters.pof.stats.visuals;

import java.util.HashMap;
import java.util.Map;

import potters.pof.stats.Data;
import potters.pof.stats.chart.Chart;
import potters.pof.stats.chart.ChartType;

public class LocationCheck implements DataTask
{
	private Data data;
	private HashMap<String,Integer> count;
	private HashMap<String,Integer> happy_count = new HashMap<String,Integer>();
	private HashMap<String,Integer> sad_count = new HashMap<String,Integer>();
	private boolean checking_happy = true;
	
	//To be read twice: happy first, sad second
	public LocationCheck()
	{
		data = new Data("Country", "Happiness %", "Total Tweets");
		count = happy_count;
	}
	
	@Override
	public void Execute(Map<String, String> values) 
	{
		if(!values.containsKey("place_country_code"))
			return;
		
		String country = values.get("place_country_code");
		
		if(count.containsKey(country))
			count.put(country, count.get(country) + 1);
		else
			count.put(country, 1);
		
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
		HashMap<String, CountryInfo> total_count = new HashMap<String, CountryInfo>();

		for(String country: happy_count.keySet())
		{
			try 
			{
				CountryInfo i = new CountryInfo(happy_count.get(country), 0);
				total_count.put(country, i);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
		for(String country: sad_count.keySet())
		{
			try 
			{
				if(total_count.containsKey(country))
				{
					CountryInfo i = total_count.get(country);
					i.sad = sad_count.get(country);
				}
				else
				{
					CountryInfo i = new CountryInfo(0, sad_count.get(country));
					total_count.put(country, i);
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
		//Add rows
		for(String country: total_count.keySet())
		{
			try 
			{
				CountryInfo i = total_count.get(country);
				data.AddRow(country, i.GetHappiness(), i.GetTotal());
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
		try 
		{
			//Legend shows values between 0-100
			data.AddRow("dud", 0, 0);
			data.AddRow("dud", 100, 0);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public Chart GetChart() 
	{
		return new Chart(ChartType.GEOGRAPHICAL, data);
	}
}


class CountryInfo
{
	int happy = 0;
	int sad = 0;
	
	public CountryInfo(){}
	public CountryInfo(int happy, int sad) 
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