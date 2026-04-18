package ast;

import types.*;

public class AstExpNil extends AstExp
{
	// constructor
	public AstExpNil(int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		// System.out.print("====================== exp -> NIL\n");
		this.lineNumber = lineNumber;
	}

	// print
	public void printMe()
	{
		System.out.print("AST NODE NIL\n");
		AstGraphviz.getInstance().logNode(serialNumber, "NIL");
	}

	// semant
	public Type semantMe()
	{
		return TypeNil.getInstance();
	}

	public temp.Temp irMe()
	{
		temp.Temp dst = temp.TempFactory.getInstance().getFreshTemp();
		ir.Ir.getInstance().AddIrCommand(new ir.IRcommandConstInt(dst, 0));
		return dst;
	}
}

