package ast;

import ir.*;
import temp.*;
import types.*;

public class AstStmtCallExp extends AstStmt
{
	public AstExpCall callExp;

	// constructor
	public AstStmtCallExp(AstExpCall callExp, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		// System.out.print("====================== stmt -> callExp SEMICOLON\n");
		this.callExp = callExp;
		this.lineNumber = lineNumber;
	}

	// print
	public void printMe()
	{
		System.out.print("AST NODE CALL EXP STMT\n");

		if (callExp != null) callExp.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "CALL\nSTMT");

		if (callExp != null) AstGraphviz.getInstance().logEdge(serialNumber, callExp.serialNumber);
	}

	// semant
	public Type semantMe() throws SemanticException
	{
		if (callExp != null)
		{
			callExp.semantMe();
		}

		return null;
	}

	public Temp irMe()
	{
		if (callExp != null) callExp.irMe();

		return null;
	}
}

