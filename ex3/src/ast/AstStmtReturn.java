package ast;

public class AstStmtReturn extends AstStmt
{
	public AstExp exp;  // can be null for void return

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtReturn(AstExp exp)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		System.out.print("====================== stmt -> RETURN exp SEMICOLON\n");
		this.exp = exp;
	}

	/*********************************************************/
	/* The printing message for a return statement AST node */
	/*********************************************************/
	public void printMe()
	{
		System.out.print("AST NODE RETURN STMT\n");

		if (exp != null) exp.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "RETURN");
		
		if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
	}
}

