package menthallab.wafflelib;

import java.io.*;

public class DatasetManager
{
	public static Dataset loadFromFile(String filePath) throws IOException
	{
		Dataset dataset = new Dataset();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		
		// Read line with attribute names and add attributes to data set
		String attributesLine = reader.readLine();
		String[] attributeNames = attributesLine.split(",");
		for (String attributeName : attributeNames)
			if (!attributeName.equals("class"))
				dataset.addAttribute(attributeName);
		
		// Read instances
		String instanceLine = null;
		while (null != (instanceLine = reader.readLine()))
		{
			String[] values = instanceLine.split(",");
			String label = values[0];
			RawInstance rawInstance = new RawInstance();
			for (int i = 1; i < values.length; ++i)
				rawInstance.add(Double.parseDouble(values[i]));
			dataset.addRawInstance(rawInstance, label);
		}
		
		reader.close();
		
		return dataset;
	}
	
	public static void saveToFile(Dataset dataset, String filePath) throws IOException
	{
	    FileOutputStream fos = new FileOutputStream(filePath);
	    PrintStream printStream = new PrintStream(fos);
	    StringBuilder sb = new StringBuilder();
	    
	    // Write line with attribute names
	    sb.append("class,");
	    for (String attributeName : dataset.getAttributes())
	    	sb.append(attributeName + ",");
	    sb.delete(sb.length() - 1, sb.length());
	    printStream.println(sb.toString());
	    
	    // Write instances
	    for (int i = 0; i < dataset.size(); ++i)
	    {
	    	sb.delete(0, sb.length());
	    	String label = dataset.getLabel(i);
	    	sb.append(label + ",");
	    	RawInstance rawInstance = dataset.getRawInstance(i);
	    	for (double value : rawInstance.getValues())
	    	{
	    		sb.append(value);
	    		sb.append(",");
	    	}
	    	sb.delete(sb.length() - 1, sb.length());
	    	printStream.println(sb.toString());
	    }
	    
	    printStream.close();
	}
}