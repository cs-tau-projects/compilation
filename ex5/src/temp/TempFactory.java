package temp;

// temp factory
public class TempFactory
{
	private int counter=0;
	
	public Temp getFreshTemp()
	{
		return new Temp(counter++);
	}
	
	// singleton
	private static TempFactory instance = null;

	protected TempFactory() {}

	public static TempFactory getInstance()
	{
		if (instance == null)
		{
			// singleton instance
			instance = new TempFactory();
		}
		return instance;
	}
}
