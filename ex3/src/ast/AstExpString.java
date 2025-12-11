package ast;

import types.*;

public class AstExpString extends AstExp
{
	public String value;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstExpString(String value, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		System.out.format("====================== exp -> STRING( %s )\n", value);
		this.value = value;
		this.lineNumber = lineNumber;
	}

	/***************************************************/
	/* The printing message for a string exp AST node */
	/***************************************************/
	public void printMe()
	{
		System.out.format("AST NODE STRING( %s )\n", value);
		AstGraphviz.getInstance().logNode(serialNumber, String.format("STRING(%s)", value));
	}

	public Type semantMe()
	{
		return TypeString.getInstance();
	}
}

