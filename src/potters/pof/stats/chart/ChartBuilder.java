package potters.pof.stats.chart;

public class ChartBuilder 
{
	private static String BuildHTMLFromParts(String data, String options, String draws, String elements)
	{
		String output = "<html>";
		output += "<head>";

		output += "<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>";
		output += "<script type=\"text/javascript\">";
		
		//Google API
		output += "google.charts.load('visualization', '1.0', {'packages':['corechart']});";
		output += "google.charts.setOnLoadCallback(drawChart);";
		
		//Draw function
		output += "function drawChart() {";
		output += data;
		output += options;
		output += draws;
		output += "}";
		output += "</script>";
		output += "</head>";

		output += "<body>";
		output += elements;
		output += "</body>";
		
		output += "</html>";
		return output;	
	}
	
	public static String BuildHTML(Chart... charts)
	{
		String data = "";
		String options = "";
		String draws = "";
		String elements = "";
		
		int ID = 0;
		for(Chart chart: charts)
		{
			data += "var data_" + ID + " = google.visualization.arrayToDataTable([" + chart.GetDataString() + "]);";
			options += "var option_" + ID + " = " + chart.getOptionsString() + ";";

			draws += "var chart_" + ID + " = new " + chart.GetTypeString() + "(document.getElementById('chart_div_" + ID + "'));";
			draws += "chart_" + ID + ".draw(data_" + ID + ",option_" + ID + ");"; 
			
			elements += "<div id=\"chart_div_" + ID + "\" align='center'></div><hr class=\"line\">";			
			ID++;
		}
		
		return BuildHTMLFromParts(data, options, draws, elements);		
	}
	
}
