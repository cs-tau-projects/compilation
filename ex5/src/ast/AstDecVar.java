package ast;

import ir.*;
import temp.*;
import types.*;
import symboltable.*;

public class AstDecVar extends AstNode {
    public String id;
    public AstType type;
    public AstExp exp = null;
    
    // The scope offset captured during semantic analysis for use in IR generation
    private int scopeOffset = -1;
    public boolean isGlobal = false;

    public AstDecVar(String id, AstType type, int lineNumber) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.id = id;
        this.type = type;
        this.lineNumber = lineNumber;
    }

    public AstDecVar(String id, AstType type, AstExp exp, int lineNumber) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.id = id;
        this.type = type;
        this.exp = exp;
        this.lineNumber = lineNumber;
    }

    public void printMe() {
        System.out.print("AST NODE VAR DEC\n");
        System.out.print("VAR NAME: " + id + "\n");
        if (type != null) type.printMe();
        if (exp != null) exp.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, String.format("VAR DEC\n%s", id));

        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }

    public Type semantMe() throws SemanticException
	{
		Type t;

		// check keyword and type
		TypeUtils.checkNotReservedKeyword(id, lineNumber);

		t = SymbolTable.getInstance().find(type.typeName);
		if (t == null)
		{
			throw new SemanticException("non existing type " + type.typeName, lineNumber);
		}

		// ensure not void and check for collisions
		if (t instanceof TypeVoid)
		{
			throw new SemanticException("variable cannot have void type", lineNumber);
		}

		if (SymbolTable.getInstance().findInCurrentScope(id) != null)
		{
			throw new SemanticException("variable " + id + " already exists in scope", lineNumber);
		}

		// check initialization
		if (exp != null)
		{
			Type expType = exp.semantMe();

			if (!TypeUtils.canAssignType(t, expType))
			{
				throw new SemanticException("type mismatch in variable initialization", lineNumber);
			}
		}

		// register variable
		SymbolTable.getInstance().enter(id, t);
		
		this.scopeOffset = SymbolTable.getInstance().getScopeOffset(id);
		this.isGlobal = SymbolTable.getInstance().isGlobalScope();

		// Return null as variable declarations don't have a value type in this context
		return null;
	}

	public Temp irMe()
	{
		// Ensure scope offset is initialized before allocation
		if (scopeOffset == -1)
		{
			scopeOffset = SymbolTable.getInstance().getScopeOffset(id);
		}

		Ir.getInstance().AddIrCommand(new IrCommandAllocate(id, scopeOffset, isGlobal));

		if (exp != null)
		{
			Ir.getInstance().AddIrCommand(new IrCommandStore(id, scopeOffset, exp.irMe(), isGlobal));
		}
		return null;
	}
}
