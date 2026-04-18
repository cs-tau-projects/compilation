package ast;

import ir.*;
import temp.*;
import types.*;
import symboltable.*;

public class AstVarSimple extends AstVar {
	// members
	public String name;

	// offsets and flags
	private int scopeOffset = -1;
	public boolean isGlobal = false;
	public boolean isField = false;
	public TypeClass fieldOwnerClass = null;
	public int thisScopeOffset = -1;

	// constructor
	public AstVarSimple(String name, int lineNumber) {
		serialNumber = AstNodeSerialNumber.getFresh();
		this.name = name;
		this.lineNumber = lineNumber;
	}

	// print
	public void printMe() {
		System.out.format("AST NODE SIMPLE VAR( %s )\n", name);

		AstGraphviz.getInstance().logNode(
				serialNumber,
				String.format("SIMPLE\nVAR\n(%s)", name));
	}

	// semant
	public Type semantMe() throws SemanticException {
		// lookup in symbol table
		Type t = SymbolTable.getInstance().find(name);

		if (t == null) {
			throw new SemanticException("undefined variable " + name, lineNumber);
		}

		this.scopeOffset = SymbolTable.getInstance().getScopeOffset(name);

		SymbolTableEntry entry = SymbolTable.getInstance().findEntry(name);
		if (entry != null) { // If it has no SCOPE-BOUNDARY below it, it's global
			boolean global = true;
			for (SymbolTableEntry e = entry; e != null; e = e.prevtop) {
				if ("SCOPE-BOUNDARY".equals(e.name)) {
					global = false;
					break;
				}
			}
			this.isGlobal = global;
		}

		this.type = t;
		if (t instanceof TypeField) {
			this.isField = true;
			this.type = ((TypeField) t).fieldType;
			Type thisType = SymbolTable.getInstance().find("this");
			if (thisType instanceof TypeClass) {
				this.fieldOwnerClass = (TypeClass) thisType;
				this.thisScopeOffset = SymbolTable.getInstance().getScopeOffset("this");
			}
		}

		return this.type;
	}

	// ir generation
	public Temp irMe() {
		Temp dst = TempFactory.getInstance().getFreshTemp();
		if (isField) {
			Temp thisTemp = TempFactory.getInstance().getFreshTemp();
			Ir.getInstance().AddIrCommand(new IrCommandLoad(thisTemp, "this", thisScopeOffset, false));
			Ir.getInstance().AddIrCommand(new IrCommandCheckNull(thisTemp));
			int fieldOffset = types.TypeUtils.getFieldOffset(fieldOwnerClass, name);
			Ir.getInstance().AddIrCommand(new IrCommandFieldGet(dst, thisTemp, fieldOffset));
		} else {
			// Use the captured scope offset (fallback to lookup if necessary)
			if (scopeOffset == -1) {
				scopeOffset = SymbolTable.getInstance().getScopeOffset(name);
			}

			Ir.getInstance().AddIrCommand(new IrCommandLoad(dst, name, scopeOffset, isGlobal));
		}
		return dst;
	}

	public int getScopeOffset() {
		return scopeOffset;
	}
}
