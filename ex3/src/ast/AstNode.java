package ast;

import types.*;

public abstract class AstNode
{
	/*******************************************/
	/* The serial number is for debug purposes */
	/* In particular, it can help in creating  */
	/* a graphviz dot format of the AST ...    */
	/*******************************************/
	public int serialNumber;

	/*******************************************/
	/* Line number for error reporting         */
	/* Used by SemanticException               */
	/*******************************************/
	public int lineNumber = -1;

	/***********************************************/
	/* The default message for an unknown AST node */
	/***********************************************/
	public void printMe()
	{
		System.out.print("AST NODE UNKNOWN\n");
	}

	/**********************************/
	/* GET A UNIQUE SERIAL NUMBER ... */
	/**********************************/
	public static int getFreshSerialNumber()
	{
		return AstNodeSerialNumber.getFresh();
	}

	/***********************************************/
	/* The default semantic action for an AST node */
	/***********************************************/
	public Type semantMe() throws SemanticException
	{
		return null;
	}
}
