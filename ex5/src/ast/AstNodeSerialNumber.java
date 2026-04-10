package ast;

public class AstNodeSerialNumber
{
	/*******************************************/
	/* The serial number is for debug purposes */
	/* In particular, it can help in creating  */
	/* a graphviz dot format of the AST ...    */
	/*******************************************/
	public int serialNumber;
	
	// --- Singleton Implementation ---
	private static AstNodeSerialNumber instance = null;

	protected AstNodeSerialNumber() {}

	private static AstNodeSerialNumber getInstance()
	{
		if (instance == null)
		{
			instance = new AstNodeSerialNumber();
			instance.serialNumber = 0;
		}
		return instance;
	}

	public int get()
	{
		return serialNumber++;
	}

	public static int getFresh()
	{
		return AstNodeSerialNumber.getInstance().get();
	}
}
