package potters.pof.stats.visuals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import potters.pof.stats.Data;
import potters.pof.stats.chart.Chart;
import potters.pof.stats.chart.ChartType;

public class UniqueWordFrequencyCheck implements DataTask
{
	private Data happy_data = new Data("Word", "Count");
	private Data sad_data = new Data("Word", "Count");
	private HashMap<String,Integer> happy_count = new HashMap<String,Integer>();
	private HashMap<String,Integer> sad_count = new HashMap<String,Integer>();
	private final String search_lang;
	private final int word_count;
	private boolean checking_happy = true;
	
	
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
	

	public UniqueWordFrequencyCheck(String search_lang, int word_count)
	{
		this.happy_data.title = "Common count to Happy";
		this.sad_data.title = "Common count to Sad";
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
						
			if(checking_happy)
			{
				//Add to happy remove from sad
				if(happy_count.containsKey(word))
					happy_count.put(word, happy_count.get(word) + 1);
				else
					happy_count.put(word, 1);
				
				if(sad_count.containsKey(word))
					sad_count.put(word, sad_count.get(word) - 1);
				else
					sad_count.put(word, -1);
			}
			else
			{
				//Add to sad remove from happy
				if(happy_count.containsKey(word))
					happy_count.put(word, happy_count.get(word) - 1);
				else
					happy_count.put(word, -1);
				
				if(sad_count.containsKey(word))
					sad_count.put(word, sad_count.get(word) + 1);
				else
					sad_count.put(word, 1);
			}
		}
	}

	@Override
	public void OnComplete() 
	{		
		if(checking_happy)
		{
			checking_happy = false;
			return;
		}

		for(String word: happy_count.keySet())
		{
			try 
			{
				happy_data.AddRow(word, happy_count.get(word));
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		happy_data.CullSet(word_count, "Count", true);
		
		
		for(String word: sad_count.keySet())
		{
			try 
			{
				sad_data.AddRow(word, sad_count.get(word));
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		sad_data.CullSet(word_count, "Count", true);
	}

	@Override
	@Deprecated
	public Chart GetChart() 
	{
		return null;
	}
	
	public Chart GetHappyChart() 
	{
		return new Chart(ChartType.PIE, happy_data);
	}
	
	public Chart GetSadChart() 
	{
		return new Chart(ChartType.PIE, sad_data);
	}
}