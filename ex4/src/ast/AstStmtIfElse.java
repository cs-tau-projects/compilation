package ast;

import symboltable.*;
import temp.Temp;
import types.*;

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
	@Override
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
	@Override
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

	@Override
	public Temp irMe(){
		// Needed in the future but not in ex4
		return null;
	}
}

