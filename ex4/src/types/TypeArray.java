package types;

/**
 * Represents an array type in the L language.
 * An array type is defined over an element type.
 * For example: array IntArray = int[];
 */
public class TypeArray extends Type
{
	/********************************************/
	/* The type of elements stored in the array */
	/********************************************/
	public Type elementType;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TypeArray(String name, Type elementType)
	{
		this.name = name;
		this.elementType = elementType;
	}

	/*************/
	/* isArray() */
	/*************/
	@Override
	public boolean isArray()
	{
		return true;
	}
}

