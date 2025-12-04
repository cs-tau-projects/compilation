package ast;

public class AstStmtAssignNew extends AstStmt
{
	/*********************/
	/*  var := newExp    */
	/*********************/
	public AstVar var;
	public AstExpNew newExp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtAssignNew(AstVar var, AstExpNew newExp)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		System.out.print("====================== stmt -> var ASSIGN newExp SEMICOLON\n");
		this.var = var;
		this.newExp = newExp;
	}

	/***************************************************************/
	/* The printing message for an assign new statement AST node */
	/***************************************************************/
	public void printMe()
	{
		System.out.print("AST NODE ASSIGN NEW STMT\n");

		if (var != null) var.printMe();
		if (newExp != null) newExp.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "ASSIGN\nleft := new ...");
		
		if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
		if (newExp != null) AstGraphviz.getInstance().logEdge(serialNumber, newExp.serialNumber);
	}
}

