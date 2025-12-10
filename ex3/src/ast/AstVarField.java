package ast;

import types.*;

public class AstVarField extends AstVar
{
	public AstVar var;
	public String fieldName;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstVarField(AstVar var, String fieldName)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> var DOT ID( %s )\n",fieldName);

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
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
		System.out.print("AST NODE FIELD VAR\n");

		/**********************************************/
		/* RECURSIVELY PRINT VAR, then FIELD NAME ... */
		/**********************************************/
		if (var != null) var.printMe();
		System.out.format("FIELD NAME( %s )\n",fieldName);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			String.format("FIELD\nVAR\n...->%s",fieldName));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var != null) AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
	}

	/********************************************************/
	/* Semantic analysis for field access (var.field)      */
	/* Looks up the field in the class hierarchy           */
	/********************************************************/
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
		Type member = TypeUtils.findMemberInClassHierarchy(tc, fieldName);

		if (member == null)
		{
			throw new SemanticException("field " + fieldName + " does not exist in class " + tc.name, lineNumber);
		}

		/*********************************************/
		/* [5] Return the type of the member        */
		/*********************************************/
		// If it's a field, return the field type
		if (member instanceof TypeField)
		{
			return ((TypeField) member).fieldType;
		}
		// If it's a method, return the function type
		return member;
	}
}
