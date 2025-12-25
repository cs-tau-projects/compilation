package ast;

import temp.*;
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
	@Override
	public void printMe()
	{
		System.out.print("AST NODE NIL\n");
		AstGraphviz.getInstance().logNode(serialNumber, "NIL");
	}

	/************************************************/
	/* Semantic analysis for nil expression        */
	/************************************************/
	@Override
	public Type semantMe()
	{
		return TypeNil.getInstance();
	}

	@Override
	public Temp irMe()
	{
		// Nil does not have a runtime representation
		return null;
	}
}

