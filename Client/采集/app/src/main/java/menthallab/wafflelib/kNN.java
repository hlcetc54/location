package menthallab.wafflelib;

import java.util.*;

public class kNN implements Classifier
{
	private final static int DEFAULT_K = 9;
	
	private Dataset dataset;
	private final int k;
	
	public kNN()
	{
		this.dataset = null;
		this.k = DEFAULT_K;
	}
	
	public kNN(int k)
	{
		this.dataset = null;
		this.k = (k > 0 ? k : DEFAULT_K);
	}
	
	public void learn(Dataset dataset)
	{
		this.dataset = dataset;
	}
	
	public void asyncLearn(Dataset dataset)
	{
		throw new UnsupportedOperationException("Not allowed for kNN!");
	}

	public boolean isCompleted()
	{
		throw new UnsupportedOperationException("Not allowed for kNN!");
	}
	
	public void stopLearning()
	{
		throw new UnsupportedOperationException("Not allowed for kNN!");
	}
	
	public double getDesiredLearningError()
	{
		throw new UnsupportedOperationException("Not allowed for kNN!");
	}
	
	public double getCurrentLearningError()
	{
		throw new UnsupportedOperationException("Not allowed for kNN!");
	}
	
	public String classify(Instance instance)
	{
		if (null == this.dataset)
			throw new NullPointerException("kNN.classify: dataset is null");;
		InstanceInfo[] kNearestInstances = findKNearestInstances(instance);
		return computeLabel(kNearestInstances);
	}
	
	// Compute distances from query instance to all instances in the data set,
	// and return information about k nearest instances
	private InstanceInfo[] findKNearestInstances(Instance queryInstance)
	{
		SortedByDistanceList orderedInstances = new SortedByDistanceList(this.k);
		List<String> datasetAttributes = this.dataset.getAttributes();
		for (int i = 0; i < this.dataset.size(); ++i)
		{
			Instance instance = this.dataset.getInstance(i);
			double sum = 0;
			for (String attributeName : datasetAttributes)
			{
				double value = instance.get(attributeName);
				Double queryValue = queryInstance.get(attributeName);
				queryValue = (null != queryValue ? queryValue : 0.0);
				sum += Math.pow(value - queryValue, 2);
			}
			double distance = Math.sqrt(sum);
			double weight = 1.0 / sum;
			String label = this.dataset.getLabel(i);
			orderedInstances.add(new InstanceInfo(distance, weight, label));
		}
		return orderedInstances.toArray();
	}

	// Compute label by weighted voting of k nearest instances
	private String computeLabel(InstanceInfo[] kNearestInstances)
	{
		String classificationLabel = null;
		double maxSum = 0;
		// Iterate by possible labels and search label, which brings maximum voting value 
		for (String label : this.dataset.getDifferentLabels())
		{
			double sum = 0;
			for (InstanceInfo instanceInfo : kNearestInstances)
				if (instanceInfo.label.equals(label))
					sum += instanceInfo.weight;
			if (sum > maxSum)
			{
				maxSum = sum;
				classificationLabel = label;
			}
		}
		return classificationLabel;
	}
}

class InstanceInfo
{
	public final double distance;
	public final double weight;
	public final String label;
	
	public InstanceInfo(double distance, double weight, String label)
	{
		this.distance = distance;
		this.weight = weight;
		this.label = label;
	}
}

class SortedByDistanceList
{
	private final int k;
	private final LinkedList<InstanceInfo> instanceInfos;

	public SortedByDistanceList(int k)
	{
		this.k = k;
		this.instanceInfos = new LinkedList<InstanceInfo>();
	}
	
	public boolean add(InstanceInfo instanceInfo)
	{
		boolean success = false;
		
		for (int i = 0; i < this.instanceInfos.size() && !success; ++i)
			if (instanceInfo.distance < this.instanceInfos.get(i).distance)
			{
				this.instanceInfos.add(i, instanceInfo);
				success = true;
			}
		if (!success && this.instanceInfos.size() < this.k)
		{
			this.instanceInfos.add(instanceInfo);
			success = true;
		}
		if (this.instanceInfos.size() > this.k)
			this.instanceInfos.removeLast();
		
		return success;
	}
	
	public InstanceInfo[] toArray()
	{
		InstanceInfo[] instanceInfoArray = new InstanceInfo[this.instanceInfos.size()];
		this.instanceInfos.toArray(instanceInfoArray);
		return instanceInfoArray;
	}
}