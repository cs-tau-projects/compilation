package ast;

import ir.*;
import temp.*;
import types.*;

public abstract class AstNode
{
	// serial number
	public int serialNumber;

	// line number for errors
	public int lineNumber = -1;

	// debug print
	public void printMe()
	{
		System.out.print("AST NODE UNKNOWN\n");
	}

	// get fresh SN
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
