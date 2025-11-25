package ast;

public class AstStmtCallExp extends AstStmt
{
	public AstExpCall callExp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtCallExp(AstExpCall callExp)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		System.out.print("====================== stmt -> callExp SEMICOLON\n");
		this.callExp = callExp;
	}

	/**************************************************************/
	/* The printing message for a call expression statement node */
	/**************************************************************/
	public void printMe()
	{
		System.out.print("AST NODE CALL EXP STMT\n");

		if (callExp != null) callExp.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "CALL\nSTMT");
		
		if (callExp != null) AstGraphviz.getInstance().logEdge(serialNumber, callExp.serialNumber);
	}
}

