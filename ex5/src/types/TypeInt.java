package types;

public class TypeInt extends Type
{
	// singleton
	private static TypeInt instance = null;

	protected TypeInt() {}

	public static TypeInt getInstance()
	{
		if (instance == null)
		{
			instance = new TypeInt();
			instance.name = "int";
		}
		return instance;
	}
}
