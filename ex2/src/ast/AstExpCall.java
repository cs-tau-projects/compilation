package ast;

public class AstExpCall extends AstExp
{
	public AstVar var;  // can be null for simple function calls
	public String funcName;
	public AstExpList params;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	// Constructor for simple function call: funcName(params)
	public AstExpCall(String funcName, AstExpList params)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.var = null;
		this.funcName = funcName;
		this.params = params;
	}

	// Constructor for method call: var.funcName(params)
	public AstExpCall(AstVar var, String funcName, AstExpList params)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.var = var;
		this.funcName = funcName;
		this.params = params;
	}

	/*************************************************/
	/* The printing message for a call exp AST node */
	/*************************************************/
	public void printMe()
	{
		System.out.print("AST NODE CALL EXP\n");

		if (var != null) var.printMe();
		System.out.format("FUNC NAME( %s )\n", funcName);
		if (params != null) params.printMe();

		String label = (var != null) ? 
			String.format("CALL\n%s.%s(...)", "var", funcName) :
			String.format("CALL\n%s(...)", funcName);

		AstGraphviz.getInstance().logNode(serialNumber, label);
		
		if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
		if (params != null) AstGraphviz.getInstance().logEdge(serialNumber, params.serialNumber);
	}
}

