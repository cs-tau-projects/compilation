package ast;

import types.*;

public class AstExpNil extends AstExp
{
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstExpNil(int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		// System.out.print("====================== exp -> NIL\n");
		this.lineNumber = lineNumber;
	}

	/************************************************/
	/* The printing message for a nil exp AST node */
	/************************************************/
	public void printMe()
	{
		System.out.print("AST NODE NIL\n");
		AstGraphviz.getInstance().logNode(serialNumber, "NIL");
	}

	/************************************************/
	/* Semantic analysis for nil expression        */
	/************************************************/
	public Type semantMe()
	{
		return TypeNil.getInstance();
	}
}

