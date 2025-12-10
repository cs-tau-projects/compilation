package ast;

import types.*;

public class AstExpList extends AstNode
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AstExp head;
	public AstExpList tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstExpList(AstExp head, AstExpList tail)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.head = head;
		this.tail = tail;
	}

	/****************************************************/
	/* The printing message for an exp list AST node */
	/****************************************************/
	public void printMe()
	{
		System.out.print("AST NODE EXP LIST\n");

		if (head != null) head.printMe();
		if (tail != null) tail.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "EXP\nLIST");
		
		if (head != null) AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
		if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
	}

	/********************************************************/
	/* Semantic analysis for expression list                */
	/* Returns a TypeList containing the types of all       */
	/* expressions in the list                              */
	/********************************************************/
	public TypeList semantMeTypeList() throws SemanticException
	{
		Type headType = null;
		TypeList tailTypeList = null;

		/****************************/
		/* [1] Analyze head expression */
		/****************************/
		if (head != null)
		{
			headType = head.semantMe();
		}

		/****************************/
		/* [2] Recursively analyze tail */
		/****************************/
		if (tail != null)
		{
			tailTypeList = tail.semantMeTypeList();
		}

		/****************************/
		/* [3] Build and return TypeList */
		/****************************/
		return new TypeList(headType, tailTypeList);
	}
}

