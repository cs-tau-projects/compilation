package ast;

import temp.*;
import types.*;

public class AstStmtVarDec extends AstStmt
{
	public AstDecVar varDec;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtVarDec(AstDecVar varDec, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.varDec = varDec;
		this.lineNumber = lineNumber;
	}

	/********************************************************/
	/* The printing message for a var dec statement node */
	/********************************************************/
	@Override
	public void printMe()
	{
		System.out.print("AST NODE VAR DEC STMT\n");

		if (varDec != null) varDec.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "VAR DEC\nSTMT");
		
		if (varDec != null) AstGraphviz.getInstance().logEdge(serialNumber, varDec.serialNumber);
	}

	@Override
	public Type semantMe() throws SemanticException
	{
		return varDec.semantMe();
	}

	@Override
	public Temp irMe() { 
		return varDec.irMe(); 
	}
}

