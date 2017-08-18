package menthallab.waffle;

public class IDGenerator
{
	private final static int INITIAL_ID = 1; 
	
	private static int id = INITIAL_ID;
	
	public static void reset(int initialValue)
	{
		id = initialValue;
	}
	
	public static void reset()
	{
		reset(INITIAL_ID);
	}
	
	public static int getNextId()
	{
		return id++;
	}
}
