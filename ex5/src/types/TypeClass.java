package types;

public class TypeClass extends Type
{
	// base class
	public TypeClass father;

	// members
	public TypeList dataMembers;
	
	// constructor
	public TypeClass(TypeClass father, String name, TypeList dataMembers)
	{
		this.name = name;
		this.father = father;
		this.dataMembers = dataMembers;
	}

	// class check
	@Override
	public boolean isClass()
	{
		return true;
	}
}
