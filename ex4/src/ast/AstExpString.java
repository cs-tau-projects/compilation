package ast;

import temp.*;
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
		this.value = value;
		this.lineNumber = lineNumber;
	}

	/***************************************************/
	/* The printing message for a string exp AST node */
	/***************************************************/
	@Override
	public void printMe()
	{
		System.out.format("AST NODE STRING( %s )\n", value);
		AstGraphviz.getInstance().logNode(serialNumber, String.format("STRING(%s)", value));
	}

	@Override
	public Type semantMe()
	{
		return TypeString.getInstance();
	}

	@Override
	public Temp irMe(){
		// Not needed for ex4 but will be needed later
		return null;
	}
}

