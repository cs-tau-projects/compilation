package types;

/**
 * Represents a field (data member) in a class.
 * Stores both the field's type and its name.
 */
public class TypeField extends Type
{
	// field name and type
	public Type fieldType;
	public ast.AstExp initExp;
	
	// constructor
	public TypeField(Type fieldType, String name, ast.AstExp initExp)
	{
		this.name = name;
		this.fieldType = fieldType;
		this.initExp = initExp;
	}
	
	public TypeField(Type fieldType, String name)
	{
		this.name = name;
		this.fieldType = fieldType;
		this.initExp = null;
	}
}

