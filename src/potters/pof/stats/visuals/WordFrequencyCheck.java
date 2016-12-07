package potters.pof.stats.visuals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import potters.pof.stats.Data;
import potters.pof.stats.chart.Chart;
import potters.pof.stats.chart.ChartType;

public class WordFrequencyCheck implements DataTask
{
	private Data data = new Data("Word", "Count");
	private HashMap<String,Integer> count = new HashMap<String,Integer>();
	private final String search_lang;
	private final int word_count;
	
	
	private static HashSet<String> blacklisted_words = new HashSet<String>(
			Arrays.asList(
					":)", ":(",":d","d:",":/", ":-)", ":-(",";)",";d",";p",
					
					"rt", "(", ")",
					
					"a", "for", "the", "this", "i", "to", "and", "my", "in", "it", "of", "so", "is", "that",
					"me", "with", "are", "was", "like", "on", "be", "if", "your", "you", "at", "i\"m", "u", "",
					"it\"s", "it\"\"s"
			)
	);
	
	private static boolean IsBlackListed(String word)
	{
		return (blacklisted_words.contains(word) || word.length() < 3
				|| word.isEmpty() || word.startsWith("http") || word.startsWith("?") || word.startsWith("<") || word.startsWith(">")
				|| word.startsWith(":)") || word.startsWith(":(")
				);
	}
	

	public WordFrequencyCheck(String title, String search_lang, int word_count)
	{
		this.data.title = title;
		this.search_lang = search_lang;
		
		if(word_count < 10)
			this.word_count = 10;
		else if(word_count > 100)
			this.word_count = 100;
		else
			this.word_count = word_count;
	}

	@Override
	public void Execute(Map<String, String> values) 
	{
		if(!values.containsKey("text") || !values.containsKey("language") )
			return;
		
		String text = values.get("text");
		String lang = values.get("language");				
		
		if(!search_lang.isEmpty() && !lang.equalsIgnoreCase(search_lang))
			return;
		
		for(String word: text.split(" "))
		{				
			word = word.replace("\n", "");
			word = word.replace("\r", "");
			word = word.replace("\b", "");
			word = word.replace("\t", "");
			word = word.replace("\f", "");
			word = word.replace("\"", "");
			word = word.replace("'", "");
			word = word.trim();
			word = word.toLowerCase();
								
			if(IsBlackListed(word))
				continue;
								
			if(count.containsKey(word))
				count.put(word, count.get(word) + 1);
			else
				count.put(word, 1);
		}
	}

	@Override
	public void OnComplete() 
	{		
		for(String word: count.keySet())
		{
			try 
			{
				data.AddRow(word, count.get(word));
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		data.CullSet(word_count, "Count", true);
	}

	@Override
	public Chart GetChart() 
	{
		return new Chart(ChartType.PIE, data);
	}
}
