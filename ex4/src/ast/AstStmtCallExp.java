package ast;

import ir.*;
import temp.*;
import types.*;

public class AstStmtCallExp extends AstStmt
{
	public AstExpCall callExp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtCallExp(AstExpCall callExp, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		// System.out.print("====================== stmt -> callExp SEMICOLON\n");
		this.callExp = callExp;
		this.lineNumber = lineNumber;
	}

	/**************************************************************/
	/* The printing message for a call expression statement node */
	/**************************************************************/
	public void printMe()
	{
		System.out.print("AST NODE CALL EXP STMT\n");

		if (callExp != null) callExp.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "CALL\nSTMT");

		if (callExp != null) AstGraphviz.getInstance().logEdge(serialNumber, callExp.serialNumber);
	}

	/********************************************************/
	/* Semantic analysis for call expression statement     */
	/* Simply delegates to the call expression             */
	/********************************************************/
	public Type semantMe() throws SemanticException
	{
		if (callExp != null)
		{
			callExp.semantMe();
		}

		/********************************************************/
		/* Return value is irrelevant for call statement       */
		/********************************************************/
		return null;
	}

	public Temp irMe()
	{
		if (callExp != null) callExp.irMe();

		return null;
	}
}

