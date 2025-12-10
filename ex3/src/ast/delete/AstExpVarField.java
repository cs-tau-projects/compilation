package ast;

import types.*;

public class AstExpVarField extends AstExpVar
{
	public AstExpVar var;
	public String fieldName;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstExpVarField(AstExpVar var, String fieldName)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		System.out.format("====================== var -> var DOT ID( %s )\n",fieldName);
		this.var = var;
		this.fieldName = fieldName;
	}

	/*************************************************/
	/* The printing message for a field var AST node */
	/*************************************************/
	public void printMe()
	{
		/*********************************/
		/* AST NODE TYPE = AST FIELD VAR */
		/*********************************/
		System.out.format("FIELD\nNAME\n(___.%s)\n",fieldName);

		/**********************************************/
		/* RECURSIVELY PRINT VAR, then FIELD NAME ... */
		/**********************************************/
		if (var != null) var.printMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("FIELD\nVAR\n___.%s",fieldName));

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var  != null) AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
	}

	public Type semantMe() throws SemanticException
	{
		Type t = null;
		TypeClass tc = null;

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
		/* [3] Make sure type is a class */
		/*********************************/
		if (!t.isClass())
		{
			throw new SemanticException("cannot access field " + fieldName + " of non-class variable", lineNumber);
		}

		tc = (TypeClass) t;

		/**************************************************************/
		/* [4] Look for fieldName in class and parent class hierarchy */
		/**************************************************************/
		TypeClass currentClass = tc;
		while (currentClass != null)
		{
			for (TypeList it = currentClass.dataMembers; it != null; it = it.tail)
			{
				if (it.head.name.equals(fieldName))
				{
					return it.head;
				}
			}
			// Move to parent class
			currentClass = currentClass.father;
		}

		/*********************************************/
		/* [5] fieldName does not exist in class hierarchy */
		/*********************************************/
		throw new SemanticException("field " + fieldName + " does not exist in class " + tc.name, lineNumber);
	}
}
