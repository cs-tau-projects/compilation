package ast;

import temp.*;
import types.*;

public class AstExpVar extends AstExp
{
	public AstVar var;

	// constructor
	public AstExpVar(AstVar var, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.var = var;
		this.lineNumber = lineNumber;
	}
	
	// debug print
	public void printMe()
	{
		System.out.print("AST NODE EXP VAR\n");
		if (var != null) var.printMe();
		
		AstGraphviz.getInstance().logNode(serialNumber, "EXP\nVAR");
		if (var != null) AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
	}

	// Delegates cleanup during semantic analysis and IR generation
	public Type semantMe() throws SemanticException
	{
		if (var == null)
		{
			throw new SemanticException("variable expression has no variable", lineNumber);
		}
		return var.semantMe();
	}

	// ir generation
	public Temp irMe()
	{
		if (var != null)
		{
			return var.irMe();
		}
		return null;
	}
}
