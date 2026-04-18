package ast;

import ir.*;
import temp.*;
import types.*;

public class AstDecList extends AstNode
{
	// members
	public AstDec head;
	public AstDecList tail;

	// constructor
	public AstDecList(AstDec head, AstDecList tail, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.head = head;
		this.tail = tail;
		this.lineNumber = lineNumber;
	}

	// print
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
		// recursive semant
		if (head != null) head.semantMe();
		if (tail != null) tail.semantMe();

		return null;
	}

	public Temp irMe()
	{
		Ir.getInstance().AddIrCommand(new IrCommandLabel("main"));
		
		// globals
		AstDecList current = this;
		while (current != null)
		{
			if (current.head != null && current.head.decNode instanceof AstDecVar)
			{
				current.head.irMe();
			}
			current = current.tail;
		}

        // user_main
        Temp dst = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandCallFunc(dst, "user_main"));
        Ir.getInstance().AddIrCommand(new IrCommandExit());

		// functions etc
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
