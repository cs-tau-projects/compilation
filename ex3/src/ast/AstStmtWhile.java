package ast;

import types.*;
import symboltable.*;

public class AstStmtWhile extends AstStmt
{
	public AstExp cond;
	public AstStmtList body;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtWhile(AstExp cond, AstStmtList body, int lineNumber)
	{
		this.cond = cond;
		this.body = body;
		this.lineNumber = lineNumber;
	}

	/********************************************************/
	/* Semantic analysis for while statement                */
	/* Checks that condition is int and analyzes body       */
	/* in a new scope                                       */
	/********************************************************/
	public Type semantMe() throws SemanticException
	{
		/****************************/
		/* [1] Check condition type */
		/****************************/
		if (cond != null)
		{
			Type condType = cond.semantMe();

			if (condType != TypeInt.getInstance())
			{
				throw new SemanticException("condition inside WHILE is not integral", lineNumber);
			}
		}

		/****************************/
		/* [2] Analyze body in new scope */
		/****************************/
		SymbolTable.getInstance().beginScope();

		if (body != null)
		{
			body.semantMe();
		}

		SymbolTable.getInstance().endScope();

		return null;
	}
}