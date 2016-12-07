package potters.pof.stats;

enum ValueType
{
	INT, DOUBLE, STRING
}

public class Value 
{
	private ValueType type;
	
	private String str_value;
	private double dbl_value;
	private int int_value;
	
	ValueType GetType() { return type; }

	Value(String value)
	{
		str_value = value;
		type = ValueType.STRING;
	}
	
	Value(double value)
	{
		dbl_value = value;
		type = ValueType.DOUBLE;
	}
	
	Value(int value)
	{
		int_value = value;
		type = ValueType.INT;
	}

	String GetAsString()
	{
		return type == ValueType.STRING ? str_value : "NULL";	
	}
	
	double GetAsDouble()
	{
		return type == ValueType.DOUBLE ? dbl_value : 0.0;	
	}
	
	int GetAsInt()
	{
		return type == ValueType.INT ? int_value : 0;	
	}
	
	boolean IsGreaterThan(Value other)
	{

		if(other.type != type)
			return false;
		
		switch(type)
		{
		case STRING:
			return GetAsString().compareTo(other.GetAsString()) == 1;
			
		case DOUBLE:
			return GetAsDouble() > other.GetAsDouble();
			
		case INT:
			return GetAsInt() > other.GetAsInt();
		}
		
		return false;
	}	
	
	boolean IsLessThan(Value other)
	{

		if(other.type != type)
			return false;
		
		switch(type)
		{
		case STRING:
			return GetAsString().compareTo(other.GetAsString()) == -1;
			
		case DOUBLE:
			return GetAsDouble() < other.GetAsDouble();
			
		case INT:
			return GetAsInt() < other.GetAsInt();
		}
		
		return false;
	}
}
