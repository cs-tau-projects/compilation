package types;

public class TypeVoid extends Type
{
	// singleton
	private static TypeVoid instance = null;

	protected TypeVoid() {}

	public static TypeVoid getInstance()
	{
		if (instance == null)
		{
			instance = new TypeVoid();
			instance.name = "void";
		}
		return instance;
	}
}
