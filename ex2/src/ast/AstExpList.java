package ast;

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
}

