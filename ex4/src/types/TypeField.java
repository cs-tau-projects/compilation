package types;

/**
 * Represents a field (data member) in a class.
 * Stores both the field's type and its name.
 */
public class TypeField extends Type
{
	/***********************************/
	/* The type of the field           */
	/***********************************/
	public Type fieldType;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TypeField(Type fieldType, String name)
	{
		this.name = name;
		this.fieldType = fieldType;
	}
}

