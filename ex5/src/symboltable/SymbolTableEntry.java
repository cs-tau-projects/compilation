package symboltable;

import types.*;

public class SymbolTableEntry {
	// index
	int index;

	// name
	public String name;

	// type
	public Type type;

	// linked list pointers
	public SymbolTableEntry prevtop;
	public SymbolTableEntry next;

	// debug info
	public int prevtopIndex;

	// constructor
	public SymbolTableEntry(
			String name,
			Type type,
			int index,
			SymbolTableEntry next,
			SymbolTableEntry prevtop,
			int prevtopIndex) {
		this.index = index;
		this.name = name;
		this.type = type;
		this.next = next;
		this.prevtop = prevtop;
		this.prevtopIndex = prevtopIndex;
	}
}
