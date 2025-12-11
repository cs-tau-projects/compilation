package ast;

import types.*;
import symboltable.*;

public class AstStmtIf extends AstStmt
{
	public AstExp cond;
	public AstStmtList body;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtIf(AstExp cond, AstStmtList body, int lineNumber)
	{
		serialNumber = AstNode.getFreshSerialNumber();
		this.cond = cond;
		this.body = body;
		this.lineNumber = lineNumber;
	}

	public Type semantMe() throws SemanticException
	{
		/****************************/
		/* [0] Semant the Condition */
		/****************************/
		if (cond.semantMe() != TypeInt.getInstance())
		{
			throw new SemanticException("condition inside IF is not integral", lineNumber);
		}
		
		/*************************/
		/* [1] Begin If Scope */
		/*************************/
		SymbolTable.getInstance().beginScope();

		/***************************/
		/* [2] Semant Data Members */
		/***************************/
		body.semantMe();

		/*****************/
		/* [3] End Scope */
		/*****************/
		SymbolTable.getInstance().endScope();

		/***************************************************/
		/* [4] Return value is irrelevant for if statement */
		/**************************************************/
		return null;		
	}
}