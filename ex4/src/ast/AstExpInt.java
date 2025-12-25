package ast;

import ir.*;
import temp.*;
import types.*;

public class AstExpInt extends AstExp
{
	public int value;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstExpInt(int value, int lineNumber)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.value = value;
		this.lineNumber = lineNumber;
	}

	/************************************************/
	/* The printing message for an int exp AST node */
	/************************************************/
	@Override
	public void printMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST INT EXP */
		/*******************************/
		System.out.format("AST NODE INT( %d )\n",value);

		/*********************************/
		/* Print to AST GRAPHVIZ DOT file */
		/*********************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			String.format("INT(%d)",value));
	}

	@Override
	public Type semantMe()
	{
		return TypeInt.getInstance();
	}

	/********************************************************/
	/* Return the constant integer value                    */
	/********************************************************/
	@Override
	public Integer tryEvaluateConstant()
	{
		return value;
	}

	@Override
	public Temp irMe()
	{
		Temp t = TempFactory.getInstance().getFreshTemp();
		Ir.getInstance().AddIrCommand(new IRcommandConstInt(t,value));
		return t;
	}
}
