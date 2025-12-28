package ast;

import ir.*;
import temp.*;
import types.*;
import symboltable.*;

public class AstStmtIfElse extends AstStmt
{
	public AstExp cond;
	public AstStmtList ifBody;
	public AstStmtList elseBody;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtIfElse(AstExp cond, AstStmtList ifBody, AstStmtList elseBody, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		// System.out.print("====================== stmt -> IF LPAREN exp RPAREN LBRACE stmtList RBRACE ELSE LBRACE stmtList RBRACE\n");
		this.cond = cond;
		this.ifBody = ifBody;
		this.elseBody = elseBody;
		this.lineNumber = lineNumber;
	}

	/************************************************************/
	/* The printing message for an if-else statement AST node */
	/************************************************************/
	public void printMe()
	{
		System.out.print("AST NODE IF-ELSE STMT\n");

		if (cond != null) cond.printMe();
		if (ifBody != null) ifBody.printMe();
		if (elseBody != null) elseBody.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "IF-ELSE");
		
		if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
		if (ifBody != null) AstGraphviz.getInstance().logEdge(serialNumber, ifBody.serialNumber);
		if (elseBody != null) AstGraphviz.getInstance().logEdge(serialNumber, elseBody.serialNumber);
	}

	/********************************************************/
	/* Semantic analysis for if-else statement              */
	/* Checks that condition is int and analyzes both       */
	/* branches in separate scopes                          */
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
				throw new SemanticException("condition inside IF is not integral", lineNumber);
			}
		}

		/****************************/
		/* [2] Analyze if body in new scope */
		/****************************/
		SymbolTable.getInstance().beginScope();

		if (ifBody != null)
		{
			ifBody.semantMe();
		}

		SymbolTable.getInstance().endScope();

		/****************************/
		/* [3] Analyze else body in new scope */
		/****************************/
		SymbolTable.getInstance().beginScope();

		if (elseBody != null)
		{
			elseBody.semantMe();
		}

		SymbolTable.getInstance().endScope();

		return null;
	}

	public Temp irMe()
	{
		/*********************************/
		/* [1] Allocate 2 fresh labels   */
		/*********************************/
		String labelElse = IrCommand.getFreshLabel("else");
		String labelEnd = IrCommand.getFreshLabel("end");

		/********************/
		/* [2] cond.irMe(); */
		/********************/
		Temp condTemp = cond.irMe();

		/********************************************/
		/* [3] Jump conditionally to else branch    */
		/*     (skip if-body when condition false)  */
		/********************************************/
		Ir.
				getInstance().
				AddIrCommand(new IrCommandJumpIfEqToZero(condTemp, labelElse));

		/***********************/
		/* [4] ifBody.irMe()   */
		/***********************/
		if (ifBody != null) ifBody.irMe();

		/****************************************/
		/* [5] Jump unconditionally to end      */
		/*     (skip else-body after if-body)   */
		/****************************************/
		Ir.
				getInstance().
				AddIrCommand(new IrCommandJumpLabel(labelEnd));

		/***********************/
		/* [6] Else label      */
		/***********************/
		Ir.
				getInstance().
				AddIrCommand(new IrCommandLabel(labelElse));

		/***********************/
		/* [7] elseBody.irMe() */
		/***********************/
		if (elseBody != null) elseBody.irMe();

		/*********************/
		/* [8] End label     */
		/*********************/
		Ir.
				getInstance().
				AddIrCommand(new IrCommandLabel(labelEnd));

		/*******************/
		/* [9] return null */
		/*******************/
		return null;
	}
}
