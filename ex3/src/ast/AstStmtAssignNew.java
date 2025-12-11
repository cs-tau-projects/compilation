package ast;

import types.*;

public class AstStmtAssignNew extends AstStmt
{
	/*********************/
	/*  var := newExp    */
	/*********************/
	public AstVar var;
	public AstExpNew newExp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtAssignNew(AstVar var, AstExpNew newExp, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		System.out.print("====================== stmt -> var ASSIGN newExp SEMICOLON\n");
		this.var = var;
		this.newExp = newExp;
		this.lineNumber = lineNumber;
	}

	/***************************************************************/
	/* The printing message for an assign new statement AST node */
	/***************************************************************/
	public void printMe()
	{
		System.out.print("AST NODE ASSIGN NEW STMT\n");

		if (var != null) var.printMe();
		if (newExp != null) newExp.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "ASSIGN\nleft := new ...");

		if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
		if (newExp != null) AstGraphviz.getInstance().logEdge(serialNumber, newExp.serialNumber);
	}

	/********************************************************/
	/* Semantic analysis for assignment with new           */
	/* Special handling for array assignments:             */
	/* - For arrays: if e = new T[], then x must be of     */
	/*   type array defined over type T                    */
	/* - For classes: normal assignment rules apply        */
	/********************************************************/
	public Type semantMe() throws SemanticException
	{
		Type varType = null;
		Type newExpType = null;

		/****************************/
		/* [1] Semant var and newExp */
		/****************************/
		if (var != null) varType = var.semantMe();
		if (newExp != null) newExpType = newExp.semantMe();

		/****************************/
		/* [2] Check for null types */
		/****************************/
		if (varType == null)
		{
			throw new SemanticException("variable has no type", lineNumber);
		}
		if (newExpType == null)
		{
			throw new SemanticException("new expression has no type", lineNumber);
		}

		/********************************************************/
		/* [3] Special handling for array allocation           */
		/* According to 2.4: if e = new T[], then x must be    */
		/* of type array defined over type T                   */
		/* Note: newExp.exp != null means it's new T[size]     */
		/********************************************************/
		if (newExp.exp != null && newExpType.isArray())
		{
			// This is array allocation: new T[size]
			// varType must be an array type
			if (!varType.isArray())
			{
				throw new SemanticException("cannot assign array to non-array variable", lineNumber);
			}

			TypeArray newArrayType = (TypeArray) newExpType;
			TypeArray varArrayType = (TypeArray) varType;

			// Element types must match exactly (no subclass substitution for arrays)
			if (newArrayType.elementType != varArrayType.elementType)
			{
				throw new SemanticException("array element type mismatch in assignment", lineNumber);
			}
		}
		/********************************************************/
		/* [4] For class allocation or other cases, use        */
		/*     standard assignment compatibility rules         */
		/********************************************************/
		else
		{
			if (!TypeUtils.canAssignType(varType, newExpType))
			{
				throw new SemanticException("type mismatch in assignment: cannot assign " + newExpType.name + " to " + varType.name, lineNumber);
			}
		}

		/********************************************************/
		/* [5] Return value is irrelevant for assign statement */
		/********************************************************/
		return null;
	}
}

