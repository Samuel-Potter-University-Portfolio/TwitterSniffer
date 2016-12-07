package potters.pof.stats.chart;

import potters.pof.stats.Data;


public class Chart 
{
	private Data data;
	private ChartType type;

	public String geo_region = "";
	public String geo_colour_axis = "colors: ['red', 'yellow', 'green']";
	
	public Chart(ChartType type, Data data)
	{
		this.type = type;
		this.data = data;
	}
	
	public String GetDataString()
	{
		return data.GetVisualization();		
	}
	
	public String getOptionsString()
	{
		String output = "{";
		
		output += "title:'" + data.title + "',";
		output += "width:" + data.width + ",";
		output += "height:" + data.height + ",";
		output += "vAxis:{title:'" + data.y_axis + "'},";
		output += "hAxis:{title:'" + data.x_axis + "'},";
		
		
		switch(type)
		{
		case BAR:
			output += "bar: {groupWidth: \"95%\"},";
			output += "seriesType: 'bars',";
			break;
			
		case LINE:
			output += "legend: { position: 'bottom' },";
			break;
			
		case PIE:
			//Nothing
			break;
			
		case SCATTER:
			//Nothing
			break;
			
		case GEOGRAPHICAL:
			
			if(geo_region != null && !geo_region.isEmpty())
				output += "region: '" + geo_region +"',";
			
			output += "colorAxis: {" + geo_colour_axis + "},";
			//output += "backgroundColor: '#81d4fa',";
			//output += "datalessRegionColor: '#f8bbd0',";
			//output += "defaultColor: '#f5f5f5',";
			break;
		
		}
		
		output += "}";
		return output;		
	}
	
	public String GetTypeString()
	{
		return type.GetAPIName();		
	}
	
}
