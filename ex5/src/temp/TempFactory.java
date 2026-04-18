/***********/
/* PACKAGE */
/***********/
package temp;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/

public class TempFactory
{
	private int counter=0;
	
	public Temp getFreshTemp()
	{
		return new Temp(counter++);
	}
	
	// --- Singleton Implementation ---
	private static TempFactory instance = null;

	protected TempFactory() {}

	public static TempFactory getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new TempFactory();
		}
		return instance;
	}
}
