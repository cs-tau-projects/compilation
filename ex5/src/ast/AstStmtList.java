package ast;

import ir.*;
import temp.*;
import types.*;

public class AstStmtList extends AstNode
{
	// members
	public AstStmt head;
	public AstStmtList tail;

	// constructor
	public AstStmtList(AstStmt head, AstStmtList tail, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.head = head;
		this.tail = tail;
		this.lineNumber = lineNumber;
	}

	// print
	public void printMe()
	{
		System.out.print("AST NODE STMT LIST\n");
		if (head != null) head.printMe();
		if (tail != null) tail.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "STMT\nLIST\n");
		
		if (head != null) AstGraphviz.getInstance().logEdge(serialNumber,head.serialNumber);
		if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber,tail.serialNumber);
	}

	public Type semantMe() throws SemanticException
	{
		if (head != null) head.semantMe();
		if (tail != null) tail.semantMe();

		return null;
	}

	public Temp irMe()
	{
		if (head != null) head.irMe();
		if (tail != null) tail.irMe();

		return null;
	}
}
