package menthallab.wafflelib;

import java.util.*;

public class RawInstance
{
	private final List<Double> values;
	
	public RawInstance()
	{
		this.values = new ArrayList<Double>();
	}
	
	public void add(double value)
	{
		this.values.add(value);
	}
	
	public Double get(int index)
	{
		return (index >= 0 && index < this.values.size() ? this.values.get(index) : null);
	}
	
	public List<Double> getValues()
	{
		return Collections.unmodifiableList(this.values);
	}
	
	public int size()
	{
		return this.values.size();
	}
}