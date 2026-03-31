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
	public ast.AstExp initExp;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
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

