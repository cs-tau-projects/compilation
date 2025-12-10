package ast;

import types.*;

public class AstVarSubscript extends AstVar
{
	public AstVar var;
	public AstExp subscript;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstVarSubscript(AstVar var, AstExp subscript)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== var -> var [ exp ]\n");

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.var = var;
		this.subscript = subscript;
	}

	/*****************************************************/
	/* The printing message for a subscript var AST node */
	/*****************************************************/
	public void printMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE SUBSCRIPT VAR\n");

		/****************************************/
		/* RECURSIVELY PRINT VAR + SUBSCRIPT ... */
		/****************************************/
		if (var != null) var.printMe();
		if (subscript != null) subscript.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			"SUBSCRIPT\nVAR\n...[...]");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var       != null) AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
		if (subscript != null) AstGraphviz.getInstance().logEdge(serialNumber,subscript.serialNumber);
	}

	public Type semantMe() throws SemanticException
	{
		Type t = null;
		Type subscriptType = null;

		/******************************/
		/* [1] Recursively semant var */
		/******************************/
		if (var != null) t = var.semantMe();

		/****************************/
		/* [2] Check for null type  */
		/****************************/
		if (t == null)
		{
			throw new SemanticException("variable has no type", lineNumber);
		}

		/*********************************/
		/* [3] Make sure type is an array */
		/*********************************/
		if (!t.isArray())
		{
			throw new SemanticException("cannot subscript non-array variable", lineNumber);
		}

		/**************************************/
		/* [4] Semant the subscript expression */
		/**************************************/
		if (subscript != null)
		{
			subscriptType = subscript.semantMe();
		}

		/********************************************/
		/* [5] Check that subscript type is int     */
		/********************************************/
		if (subscriptType != TypeInt.getInstance())
		{
			throw new SemanticException("array subscript must be of type int", lineNumber);
		}

		/********************************************************/
		/* [6] Check if subscript is a constant >= 0           */
		/********************************************************/
		if (subscript instanceof AstExpInt)
		{
			AstExpInt subscriptInt = (AstExpInt) subscript;
			if (subscriptInt.value < 0)
			{
				throw new SemanticException("array subscript must be >= 0", lineNumber);
			}
		}

		/********************************************************/
		/* [7] Return the element type of the array            */
		/********************************************************/
		TypeArray arrayType = (TypeArray) t;
		return arrayType.elementType;
	}
}
