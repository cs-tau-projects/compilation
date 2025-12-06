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

    public Type semantMe()
	{
		Type t;
	
		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		t = SymbolTable.getInstance().find(type);
		if (t == null)
		{
			System.out.format(">> ERROR [%d:%d] non existing type %s\n",2,2,type);
			System.exit(0);
		}
		
		/**************************************/
		/* [2] Check That Name does NOT exist */
		/**************************************/
		if (SymbolTable.getInstance().find(name) != null)
		{
			System.out.format(">> ERROR [%d:%d] variable %s already exists in scope\n",2,2,name);				
		}

		/************************************************/
		/* [3] Enter the Identifier to the Symbol Table */
		/************************************************/
		SymbolTable.getInstance().enter(name,t);

		/************************************************************/
		/* [4] Return value is irrelevant for variable declarations */
		/************************************************************/
		return null;		
	}
}
