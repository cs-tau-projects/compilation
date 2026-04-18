package ast;

import ir.*;
import temp.*;
import types.*;

public class AstExpList extends AstNode
{
	// members
	public AstExp head;
	public AstExpList tail;

	// constructor
	public AstExpList(AstExp head, AstExpList tail, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.head = head;
		this.tail = tail;
		this.lineNumber = lineNumber;
	}

	// print
	public void printMe()
	{
		System.out.print("AST NODE EXP LIST\n");

		if (head != null) head.printMe();
		if (tail != null) tail.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "EXP\nLIST");
		
		if (head != null) AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
		if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
	}

	// semant as list
	public TypeList semantMeTypeList() throws SemanticException
	{
		Type headType = null;
		TypeList tailTypeList = null;

		// semant head
		if (head != null)
		{
			headType = head.semantMe();
		}

		// semant tail
		if (tail != null)
		{
			tailTypeList = tail.semantMeTypeList();
		}

		// build list
		return new TypeList(headType, tailTypeList);
	}

	public Temp irMe()
	{
		return head.irMe();
	}
}

