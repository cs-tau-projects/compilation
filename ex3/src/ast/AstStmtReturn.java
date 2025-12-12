package ast;

import types.*;
import symboltable.*;

public class AstStmtReturn extends AstStmt
{
	public AstExp exp;  // can be null for void return

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtReturn(AstExp exp, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		// System.out.print("====================== stmt -> RETURN exp SEMICOLON\n");
		this.exp = exp;
		this.lineNumber = lineNumber;
	}

	/*********************************************************/
	/* The printing message for a return statement AST node */
	/*********************************************************/
	public void printMe()
	{
		System.out.print("AST NODE RETURN STMT\n");

		if (exp != null) exp.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "RETURN");

		if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
	}

	/*********************************************************/
	/* Semantic analysis for return statement               */
	/*********************************************************/
	public Type semantMe() throws SemanticException
	{
		Type expectedReturnType = SymbolTable.getInstance().getCurrentFunctionReturnType();

		/******************************************/
		/* [1] Check if we're inside a function  */
		/******************************************/
		if (expectedReturnType == null)
		{
			throw new SemanticException("return statement outside of function", lineNumber);
		}

		/******************************************/
		/* [2] void function - must return empty */
		/******************************************/
		if (expectedReturnType instanceof TypeVoid)
		{
			if (exp != null)
			{
				throw new SemanticException("void function cannot return a value", lineNumber);
			}
			return null;
		}

		/************************************************/
		/* [3] non-void function - must return a value */
		/************************************************/
		if (exp == null)
		{
			throw new SemanticException("non-void function must return a value", lineNumber);
		}

		/************************************************/
		/* [4] Check return expression type matches    */
		/************************************************/
		Type actualReturnType = exp.semantMe();

		if (!TypeUtils.canAssignType(expectedReturnType, actualReturnType))
		{
			throw new SemanticException("return type mismatch", lineNumber);
		}

		return null;
	}
}

