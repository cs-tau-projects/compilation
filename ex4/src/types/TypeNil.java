package types;

/**
 * Represents the nil type in the L language.
 * nil can be assigned to any class or array type.
 */
public class TypeNil extends Type
{
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static TypeNil instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected TypeNil() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static TypeNil getInstance()
	{
		if (instance == null)
		{
			instance = new TypeNil();
			instance.name = "nil";
		}
		return instance;
	}
}

