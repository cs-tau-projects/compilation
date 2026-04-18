package ast;

import ir.*;
import temp.*;
import types.*;

public class AstExpInt extends AstExp
{
	public int value;
	
	// constructor
	public AstExpInt(int value, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.value = value;
		this.lineNumber = lineNumber;
	}

	// print
	public void printMe()
	{
		System.out.format("AST NODE INT( %d )\n",value);
		AstGraphviz.getInstance().logNode(serialNumber, String.format("INT(%d)",value));
	}

	public Type semantMe()
	{
		return TypeInt.getInstance();
	}

	// constant value
	@Override
	public Integer tryEvaluateConstant()
	{
		return value;
	}

	public Temp irMe()
	{
		Temp t = TempFactory.getInstance().getFreshTemp();
		Ir.getInstance().AddIrCommand(new IRcommandConstInt(t,value));
		return t;
	}
}
