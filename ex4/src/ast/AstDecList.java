package ast;

import ir.*;
import temp.*;
import types.*;

public class AstDecList extends AstNode
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AstDec head;
	public AstDecList tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstDecList(AstDec head, AstDecList tail, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.head = head;
		this.tail = tail;
		this.lineNumber = lineNumber;
	}

	/******************************************************/
	/* The printing message for a declaration list node */
	/******************************************************/
	public void printMe()
	{
		System.out.print("AST NODE DEC LIST\n");

		if (head != null) head.printMe();
		if (tail != null) tail.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "DEC\nLIST");
		
		if (head != null) AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
		if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
	}

	public Type semantMe() throws SemanticException
	{
		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (head != null) head.semantMe();
		if (tail != null) tail.semantMe();

		return null;
	}

	public Temp irMe()
	{
		/****************************************/
		/* IR generation with reordering:      */
		/* 1. Global variables first           */
		/* 2. Functions and classes second     */
		/****************************************/
		
		// Pass 1: Global variable declarations
		AstDecList current = this;
		while (current != null)
		{
			if (current.head != null && current.head.decNode instanceof AstDecVar)
			{
				current.head.irMe();
			}
			current = current.tail;
		}

		// Pass 2: Function declarations (and anything else)
		current = this;
		while (current != null)
		{
			if (current.head != null && !(current.head.decNode instanceof AstDecVar))
			{
				current.head.irMe();
			}
			current = current.tail;
		}

		return null;
	}
}
