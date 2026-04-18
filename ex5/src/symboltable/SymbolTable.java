package symboltable;

import java.io.PrintWriter;
import types.*;

/**
 * Symbol Table implementation using a hash table with chaining and a
 * history stack (top/prevtop) to support lexical scoping.
 */
public class SymbolTable {
	private int hashArraySize = 13;

	// Hash table array and history stack top
	private SymbolTableEntry[] table = new SymbolTableEntry[hashArraySize];
	private SymbolTableEntry top;
	private int topIndex = 0;

	// Track the current function's return type for validation of return statements
	private Type currentFunctionReturnType = null;

	// Hash function for symbol table entries
	private int hash(String s) {
		if (s.charAt(0) == 'l') {
			return 1;
		}
		if (s.charAt(0) == 'm') {
			return 1;
		}
		if (s.charAt(0) == 'r') {
			return 3;
		}
		if (s.charAt(0) == 'i') {
			return 6;
		}
		if (s.charAt(0) == 'd') {
			return 6;
		}
		if (s.charAt(0) == 'k') {
			return 6;
		}
		if (s.charAt(0) == 'f') {
			return 6;
		}
		if (s.charAt(0) == 'S') {
			return 6;
		}
		return 12;
	}

	public void enter(String name, Type t) {
		// 1. Compute the hash value for this new entry
		int hashValue = hash(name);

		// 2. Extract the current head of the bucket
		SymbolTableEntry next = table[hashValue];

		// 3. Prepare a new symbol table entry
		SymbolTableEntry e = new SymbolTableEntry(name, t, hashValue, next, top, topIndex++);

		// 4. Update the top of the symbol table stack
		top = e;

		// 5. Insert the new entry at the head of the bucket
		table[hashValue] = e;

		// 6. Print status for debugging
		printMe();
	}

	// find inner scope element
	public Type find(String name) {
		SymbolTableEntry e;

		for (e = table[hash(name)]; e != null; e = e.next) {
			if (name.equals(e.name)) {
				return e.type;
			}
		}

		return null;
	}

	/********************************************************/
	/* Find the entry (with offset info) for a variable */
	/* Returns the SymbolTableEntry for the inner-most */
	/* scope element with the given name */
	/********************************************************/
	public SymbolTableEntry findEntry(String name) {
		SymbolTableEntry e;

		for (e = table[hash(name)]; e != null; e = e.next) {
			if (name.equals(e.name)) {
				return e;
			}
		}

		return null;
	}

	/********************************************************/
	/* Get the scope offset for a variable */
	/* This is the prevtopIndex which uniquely identifies */
	/* the variable declaration in the symbol table */
	/********************************************************/
	public int getScopeOffset(String name) {
		SymbolTableEntry e = findEntry(name);
		if (e != null) {
			return e.prevtopIndex;
		}
		return -1; // Not found
	}

	/********************************************************/
	/* Find element with name in current scope only */
	/* Returns null if not found in current scope */
	/********************************************************/
	public Type findInCurrentScope(String name) {
		// Walk through the prevtop chain (entries in order of insertion)
		// starting from the most recent entry (top)
		for (SymbolTableEntry e = top; e != null; e = e.prevtop) {
			// Stop if we hit a scope boundary - we've left the current scope
			if (e.name.equals("SCOPE-BOUNDARY")) {
				return null;
			}
			// Check if this entry matches the name we're looking for
			if (name.equals(e.name)) {
				return e.type;
			}
		}

		return null;
	}

	// begin scope
	public void beginScope() {
		// scope markers
		enter(
				"SCOPE-BOUNDARY",
				new TypeForScopeBoundaries("NONE"));

		// print table
		printMe();
	}

	// end scope
	/**
	 * End current scope by popping elements until SCOPE-BOUNDARY is reached.
	 */
	public void endScope() {
		while (!top.name.equals("SCOPE-BOUNDARY")) {
			table[top.index] = top.next;
			top = top.prevtop;
		}
		// Pop the SCOPE-BOUNDARY entry itself
		table[top.index] = top.next;
		top = top.prevtop;

		printMe();
	}

	public static int n = 0;

	public void printMe() {
		int i = 0;
		int j = 0;
		String dirname = "./output/";
		String filename = String.format("SYMBOL_TABLE_%d_IN_GRAPHVIZ_DOT_FORMAT.txt", n++);

		try {
			// 1. Open Graphviz text file for writing
			PrintWriter fileWriter = new PrintWriter(dirname + filename);

			// 2. Write Graphviz dot prolog
			fileWriter.print("digraph structs {\n");
			fileWriter.print("rankdir = LR\n");
			fileWriter.print("node [shape=record];\n");

			// 3. Write Hash Table structure
			fileWriter.print("hashTable [label=\"");
			for (i = 0; i < hashArraySize - 1; i++) {
				fileWriter.format("<f%d>\n%d\n|", i, i);
			}
			fileWriter.format("<f%d>\n%d\n\"];\n", hashArraySize - 1, hashArraySize - 1);

			// 4. Loop over hash table array and print all linked lists per array cell
			for (i = 0; i < hashArraySize; i++) {
				if (table[i] != null) {
					// 4a. Print hash table array[i] -> entry(i,0) edge
					fileWriter.format("hashTable:f%d -> node_%d_0:f0;\n", i, i);
				}
				j = 0;
				for (SymbolTableEntry it = table[i]; it != null; it = it.next) {
					// 4b. Print entry(i,it) node
					fileWriter.format("node_%d_%d ", i, j);
					fileWriter.format("[label=\"<f0>%s|<f1>%s|<f2>prevtop=%d|<f3>next\"];\n",
							it.name,
							it.type.name,
							it.prevtopIndex);

					if (it.next != null) {
						// 4c. Print entry(i,it) -> entry(i,it.next) edge
						fileWriter.format(
								"node_%d_%d -> node_%d_%d [style=invis,weight=10];\n",
								i, j, i, j + 1);
						fileWriter.format(
								"node_%d_%d:f3 -> node_%d_%d:f0;\n",
								i, j, i, j + 1);
					}
					j++;
				}
			}
			fileWriter.print("}\n");
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// --- Singleton Implementation ---
	private static SymbolTable instance = null;

	protected SymbolTable() {
	}

	public static SymbolTable getInstance() {
		if (instance == null) {
			instance = new SymbolTable();

			// Initialize with primitive types
			instance.enter("int", TypeInt.getInstance());
			instance.enter("string", TypeString.getInstance());
			instance.enter("void", TypeVoid.getInstance());

			// Initialize library functions
			instance.enter(
					"PrintInt",
					new TypeFunction(
							TypeVoid.getInstance(),
							"PrintInt",
							new TypeList(
									TypeInt.getInstance(),
									null)));

			instance.enter(
					"PrintString",
					new TypeFunction(
							TypeVoid.getInstance(),
							"PrintString",
							new TypeList(
									TypeString.getInstance(),
									null)));

		}
		return instance;
	}

	// Set the current function return type (when entering a func)
	public void setCurrentFunctionReturnType(Type returnType) {
		this.currentFunctionReturnType = returnType;
	}

	// Get the current function return type (for return statements)
	public Type getCurrentFunctionReturnType() {
		return this.currentFunctionReturnType;
	}

	public boolean isGlobalScope() {
		for (SymbolTableEntry e = top; e != null; e = e.prevtop) {
			if ("SCOPE-BOUNDARY".equals(e.name)) {
				return false;
			}
		}
		return true;
	}
}
