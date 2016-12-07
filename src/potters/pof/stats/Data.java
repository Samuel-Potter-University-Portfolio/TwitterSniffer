package potters.pof.stats;

import java.util.ArrayList;
import java.util.Comparator;

public class Data 
{
	private String Column[];
	private ArrayList<Value[]> Row;
	
	public String title;
	public int width = 1280;
	public int height = 720;
	public String x_axis = "";
	public String y_axis = "";
	
	public Data(String... columns)
	{
		Column = new String[columns.length];
		
		for(int i = 0; i < columns.length; i++)
			Column[i] = columns[i];
		
		Row = new ArrayList<Value[]>();
	}
	
	
	public void AddRow(Object... values) throws Exception
	{
		Value[] row = new Value[Column.length];
		
		for(int i = 0; i<row.length; i++)
		{
			if(values[i].getClass() == String.class)
			{
				row[i] = new Value((String)values[i]);
			}
			else if(values[i].getClass() == java.lang.Double.class)
			{
				row[i] = new Value((double)values[i]);
			}
			else if(values[i].getClass() == java.lang.Integer.class)
			{
				row[i] = new Value((int)values[i]);
			}
			else
			{
				throw new Exception("Trying to add unsupported type to row: '" + values[i] + "':" + values[i].getClass());
			}			
		}	
				
		Row.add(row);
	}
	
	public String GetVisualization()
	{
		String output = "[";
		
		boolean first = true;
		for(String col: Column)
		{
			if(first)
				first = false;
			else
				output += ",";
			
			output += "'" + col + "'";
		}
		output += "]";
		
		for(Value[] row: Row)
		{
			output += ",[";
			
			first = true;
			for(Value val: row)
			{
				if(first)
					first = false;
				else
					output += ",";
				
				switch(val.GetType())
				{
				case STRING:
					output += "'" + val.GetAsString() + "'";
					break;
					
				case DOUBLE:
					output += val.GetAsDouble();
					break;
					
				case INT:
					output += val.GetAsInt();
					break;
				}
			}
			
			
			output += "]";
		}
		return output;
	}
	
	public void CullSet(final int max_rows, final String sort_column, final boolean acessending)
	{
		ArrayList<Value[]> Row = new ArrayList<Value[]>(this.Row);

		//Sort rows because it looks nicer
		{			
			int column_index = 0;
			int i = 0;
			for(String column: Column)
			{
				if(column == sort_column)
				{
					column_index = i;
					break;
				}
				i++;
			}
			final int sort_index = column_index;
			
			Row.sort(new Comparator<Value[]>()
			{
				@Override
		        public int compare(Value[] row_a, Value[] row_b)
		        {
					int val = 0;
					if(row_a[sort_index].IsGreaterThan(row_b[sort_index]))
						val++;
					if(row_a[sort_index].IsLessThan(row_b[sort_index]))
						val--;
					
		            return acessending  ? -val : val;
		        }
			});
		}
		
		//Exclude count
		if(max_rows != 0)
		{
			while(Row.size() > max_rows)
			{
				Row.remove(Row.size() - 1);
			}
		}
		
		this.Row = Row;
	}
}
