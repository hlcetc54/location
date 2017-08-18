package menthallab.wafflelib;

import java.util.*;

public class Dataset
{
	private final List<String> attributes;
	private final List<RawInstance> instances;
	private final List<String> labels;
	private final List<String> differentLabels;
	
	public Dataset()
	{
		this.attributes = new ArrayList<String>();
		this.instances = new ArrayList<RawInstance>();
		this.labels = new ArrayList<String>();
		this.differentLabels = new ArrayList<String>();
	}
	
	public Dataset(Dataset dataset)
	{
		this.attributes = new ArrayList<String>(dataset.attributes);
		this.instances = new ArrayList<RawInstance>(dataset.instances);
		this.labels = new ArrayList<String>(dataset.labels);
		this.differentLabels = new ArrayList<String>(dataset.differentLabels);
	}
	
	public boolean addAttribute(String attributeName)
	{
		if (this.attributes.contains(attributeName))
			return false;
		this.attributes.add(attributeName);
		for (RawInstance rawInstance : this.instances)
			rawInstance.add(0);
		return true;
	}
	
	public void addInstance(Instance instance, String label, boolean addNewAttributes)
	{
		if (instance.size() == 0)
			return;
		if (addNewAttributes)
			for (String attributeName : instance.getAttributes())
				this.addAttribute(attributeName);
		boolean mustBeAdded = false;
		RawInstance newRawInstance = new RawInstance();
		for (String attributeName : this.attributes)
		{
			Double value = instance.get(attributeName);
			if (null != value)
			{
				newRawInstance.add(value);
				mustBeAdded = true;
			}
			else
				newRawInstance.add(111111);
				
		}
		if (mustBeAdded)
			addNewRawInstance(newRawInstance, label);
	}
	
	public void addRawInstance(RawInstance rawInstance, String label)
	{
		if (rawInstance.size() == 0)
			return;
		if (rawInstance.size() != this.attributes.size())
			throw new IllegalArgumentException("Dataset.addRawInstance");
		addNewRawInstance(rawInstance, label);
	}
	
	private void addNewRawInstance(RawInstance rawInstance, String label)
	{
		this.instances.add(rawInstance);
		this.labels.add(label);
		if (!this.differentLabels.contains(label))
			this.differentLabels.add(label);
	}
	
	public Instance getInstance(int index)
	{
		if (index < 0 || index >= this.instances.size())
			return null;
		RawInstance rawInstance = this.instances.get(index);
		Instance instance = new Instance();
		for (int i = 0; i < this.attributes.size(); ++i)
			instance.add(this.attributes.get(i), rawInstance.get(i));
		return instance;
	}
	
	public RawInstance getRawInstance(int index)
	{
		if (index < 0 || index >= this.instances.size())
			return null;
		return this.instances.get(index);
	}
	
	public String getLabel(int index)
	{
		if (index < 0 || index >= this.labels.size())
			return null;
		return this.labels.get(index);
	}
	
	public List<String> getAttributes()
	{
		return Collections.unmodifiableList(this.attributes);
	}
	
	public List<String> getDifferentLabels()
	{
		return Collections.unmodifiableList(this.differentLabels);
	}
	
	public int size()
	{
		return this.instances.size();
	}
	
	public void shuffle()
	{
		Random rand = new Random();
		List<RawInstance> tempInstances = new ArrayList<RawInstance>(this.instances);
		List<String> tempLabels = new ArrayList<String>(this.labels);
		this.instances.clear();
		this.labels.clear();
		while (tempInstances.size() > 0)
		{
			int index = rand.nextInt(tempInstances.size());
			RawInstance instance = tempInstances.remove(index);
			String label = tempLabels.remove(index);
			this.instances.add(instance);
			this.labels.add(label);
		}
	}
}