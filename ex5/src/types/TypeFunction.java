package types;

public class TypeFunction extends Type
{
	// return type
	public Type returnType;

	// params
	public TypeList params;
	
	// constructor
	public TypeFunction(Type returnType, String name, TypeList params)
	{
		this.name = name;
		this.returnType = returnType;
		this.params = params;
	}
}
