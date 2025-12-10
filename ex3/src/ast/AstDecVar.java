package ast;

import types.*;
import symboltable.*;

public class AstDecVar extends AstNode {
    public String id;
    public AstType type;
    public AstExp exp = null;

    public AstDecVar(String id, AstType type) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.id = id;
        this.type = type;
    }

    public AstDecVar(String id, AstType type, AstExp exp) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.id = id;
        this.type = type;
        this.exp = exp;
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

		/************************************/
		/* [0] Check for reserved keyword   */
		/************************************/
		TypeUtils.checkNotReservedKeyword(id, lineNumber);

		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		t = SymbolTable.getInstance().find(type.typeName);
		if (t == null)
		{
			throw new SemanticException("non existing type " + type.typeName, lineNumber);
		}

		/******************************************/
		/* [2] Check that type is not void        */
		/******************************************/
		if (t instanceof TypeVoid)
		{
			throw new SemanticException("variable cannot have void type", lineNumber);
		}

		/**************************************/
		/* [3] Check That Name does NOT exist */
		/* in current scope                   */
		/**************************************/
		if (SymbolTable.getInstance().findInCurrentScope(id) != null)
		{
			throw new SemanticException("variable " + id + " already exists in scope", lineNumber);
		}

		/********************************************************/
		/* [4] If there's initialization, check type compatibility */
		/********************************************************/
		if (exp != null)
		{
			Type expType = exp.semantMe();

			if (!TypeUtils.canAssignType(t, expType))
			{
				throw new SemanticException("type mismatch in variable initialization", lineNumber);
			}
		}

		/************************************************/
		/* [5] Enter the Identifier to the Symbol Table */
		/************************************************/
		SymbolTable.getInstance().enter(id, t);

		/************************************************************/
		/* [6] Return value is irrelevant for variable declarations */
		/************************************************************/
		return null;
	}
}
