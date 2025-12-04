package ast;

public class AstExpNil extends AstExp
{
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstExpNil()
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		System.out.print("====================== exp -> NIL\n");
	}

	/************************************************/
	/* The printing message for a nil exp AST node */
	/************************************************/
	public void printMe()
	{
		System.out.print("AST NODE NIL\n");
		AstGraphviz.getInstance().logNode(serialNumber, "NIL");
	}
}

