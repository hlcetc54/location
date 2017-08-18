package menthallab.wafflelib;

import java.util.*;

public class Instance
{
	private final Map<String, Double> values;
	
	public Instance()
	{
		this.values = new HashMap<String, Double>();
	}
	
	public void add(String attributeName, double value)
	{
		this.values.put(attributeName, value);
	}
	
	public Double get(String attributeName)
	{
		return this.values.get(attributeName);
	}
	
	public Set<String> getAttributes()
	{
		return Collections.unmodifiableSet(this.values.keySet());
	}
	
	public int size()
	{
		return this.values.size();
	}
}