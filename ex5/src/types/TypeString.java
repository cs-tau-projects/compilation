package types;

public class TypeString extends Type
{
	// singleton
	private static TypeString instance = null;

	protected TypeString() {}

	public static TypeString getInstance()
	{
		if (instance == null)
		{
			instance = new TypeString();
			instance.name = "string";
		}
		return instance;
	}
}
