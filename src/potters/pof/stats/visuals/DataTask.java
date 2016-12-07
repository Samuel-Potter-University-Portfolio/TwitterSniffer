package potters.pof.stats.visuals;

import java.util.Map;

import potters.pof.stats.chart.Chart;

public interface DataTask 
{
	public abstract void Execute(Map<String,String> values);
	
	public abstract void OnComplete();
	
	public abstract Chart GetChart();
}
