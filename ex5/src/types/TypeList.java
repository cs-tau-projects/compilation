package types;

public class TypeList
{
	public Type head;
	public TypeList tail;

	// constructor
	public TypeList(Type head, TypeList tail)
	{
		this.head = head;
		this.tail = tail;
	}
}
