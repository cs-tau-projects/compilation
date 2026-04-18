package types;

public class TypeFunction extends Type
{
	// function name, return type and params
	public Type returnType;

	// constructor
	public TypeList params;
	
	public TypeFunction(Type returnType, String name, TypeList params)
	{
		this.name = name;
		this.returnType = returnType;
		this.params = params;
	}
}
