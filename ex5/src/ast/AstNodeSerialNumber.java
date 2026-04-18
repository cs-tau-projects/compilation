package ast;

public class AstNodeSerialNumber
{
	// serial number for graphviz / debug
	public int serialNumber;
	
	// singleton
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
