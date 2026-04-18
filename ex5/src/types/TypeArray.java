package types;

/**
 * Represents an array type in the L language.
 * An array type is defined over an element type.
 * For example: array IntArray = int[];
 */
public class TypeArray extends Type
{
	// element type
	public Type elementType;
	
	// constructor
	public TypeArray(String name, Type elementType)
	{
		this.name = name;
		this.elementType = elementType;
	}

	// array check
	@Override
	public boolean isArray()
	{
		return true;
	}
}

