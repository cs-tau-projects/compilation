package types;

public class TypeClass extends Type
{
	// null if no father class
	public TypeClass father;

	// all data members (fields + methods)
	public TypeList dataMembers;
	
	// constructor
	public TypeClass(TypeClass father, String name, TypeList dataMembers)
	{
		this.name = name;
		this.father = father;
		this.dataMembers = dataMembers;
	}

	@Override
	public boolean isClass()
	{
		return true;
	}
}
