package potters.pof.stats.chart;

public enum ChartType 
{
	
	LINE, BAR, PIE, GEOGRAPHICAL, SCATTER;

	public String GetAPIName()
	{
		switch(this)
		{
		case LINE:
			return "google.visualization.LineChart";
		case BAR:
			return "google.visualization.BarChart";
		//case COLUMN:
			//return "google.visualization.ColumnChart(";
		case PIE:
			return "google.visualization.PieChart";
		case GEOGRAPHICAL:
			return "google.visualization.GeoChart";
		case SCATTER:
			return "google.visualization.ScatterChart";
		default:
			return "";
		
		}
	}
}
