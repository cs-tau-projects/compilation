package ast;

public class AstStmtIfElse extends AstStmt
{
	public AstExp cond;
	public AstStmtList ifBody;
	public AstStmtList elseBody;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtIfElse(AstExp cond, AstStmtList ifBody, AstStmtList elseBody)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		System.out.print("====================== stmt -> IF LPAREN exp RPAREN LBRACE stmtList RBRACE ELSE LBRACE stmtList RBRACE\n");
		this.cond = cond;
		this.ifBody = ifBody;
		this.elseBody = elseBody;
	}

	/************************************************************/
	/* The printing message for an if-else statement AST node */
	/************************************************************/
	public void printMe()
	{
		System.out.print("AST NODE IF-ELSE STMT\n");

		if (cond != null) cond.printMe();
		if (ifBody != null) ifBody.printMe();
		if (elseBody != null) elseBody.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "IF-ELSE");
		
		if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
		if (ifBody != null) AstGraphviz.getInstance().logEdge(serialNumber, ifBody.serialNumber);
		if (elseBody != null) AstGraphviz.getInstance().logEdge(serialNumber, elseBody.serialNumber);
	}
}

