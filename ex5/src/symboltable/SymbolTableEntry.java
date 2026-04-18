package symboltable;

import types.*;

// table entry
public class SymbolTableEntry
{
	// fields
	int index;
	public String name;
	public Type type;

	// links
	public SymbolTableEntry prevtop;
	public SymbolTableEntry next;

	// debug
	public int prevtopIndex;
	
	// constructor
	public SymbolTableEntry(
		String name,
		Type type,
		int index,
		SymbolTableEntry next,
		SymbolTableEntry prevtop,
		int prevtopIndex)
	{
		this.index = index;
		this.name = name;
		this.type = type;
		this.next = next;
		this.prevtop = prevtop;
		this.prevtopIndex = prevtopIndex;
	}
}
