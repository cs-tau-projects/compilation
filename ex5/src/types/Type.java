package types;

public abstract class Type
{
	// type name
	public String name;

	// class check
	public boolean isClass(){ return false;}

	// array check
	public boolean isArray(){ return false;}
}
