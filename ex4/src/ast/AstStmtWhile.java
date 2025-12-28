package ast;

import ir.*;
import temp.*;
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

	public Temp irMe()
	{
		/*******************************/
		/* [1] Allocate 2 fresh labels */
		/*******************************/
		String labelEnd   = IrCommand.getFreshLabel("end");
		String labelStart = IrCommand.getFreshLabel("start");

		/*********************************/
		/* [2] entry label for the while */
		/*********************************/
		Ir.
				getInstance().
				AddIrCommand(new IrCommandLabel(labelStart));

		/********************/
		/* [3] cond.IRme(); */
		/********************/
		Temp condTemp = cond.irMe();

		/******************************************/
		/* [4] Jump conditionally to the loop end */
		/******************************************/
		Ir.
				getInstance().
				AddIrCommand(new IrCommandJumpIfEqToZero(condTemp,labelEnd));

		/*******************/
		/* [5] body.IRme() */
		/*******************/
		body.irMe();

		/******************************/
		/* [6] Jump to the loop entry */
		/******************************/
		Ir.
				getInstance().
				AddIrCommand(new IrCommandJumpLabel(labelStart));

		/**********************/
		/* [7] Loop end label */
		/**********************/
		Ir.
				getInstance().
				AddIrCommand(new IrCommandLabel(labelEnd));

		/*******************/
		/* [8] return null */
		/*******************/
		return null;
	}
}