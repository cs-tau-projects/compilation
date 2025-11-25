package ast;

public class AstExpString extends AstExp
{
	public String value;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstExpString(String value)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		System.out.format("====================== exp -> STRING( %s )\n", value);
		this.value = value;
	}

	/***************************************************/
	/* The printing message for a string exp AST node */
	/***************************************************/
	public void printMe()
	{
		System.out.format("AST NODE STRING( %s )\n", value);
		AstGraphviz.getInstance().logNode(serialNumber, String.format("STRING(%s)", value));
	}
}

