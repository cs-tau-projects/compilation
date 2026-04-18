package ast;

import ir.*;
import temp.*;
import types.*;

public abstract class AstNode
{
	// serial number for debug/graphviz
	public int serialNumber;

	// line number for errors
	public int lineNumber = -1;

	// default print
	public void printMe()
	{
		System.out.print("AST NODE UNKNOWN\n");
	}

	// get serial number
	public static int getFreshSerialNumber()
	{
		return AstNodeSerialNumber.getFresh();
	}

	// semantic analysis
	public Type semantMe() throws SemanticException
	{
		return null;
	}

	// ir generation
	public Temp irMe()
	{
		return null;
	}
}
