package ast;

import types.*;

public class AstVarSubscript extends AstVar
{
	public AstVar var;
	public AstExp subscript;

	// constructor
	public AstVarSubscript(AstVar var, AstExp subscript, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.var = var;
		this.subscript = subscript;
		this.lineNumber = lineNumber;
	}

	// debug print
	public void printMe()
	{
		System.out.print("AST NODE SUBSCRIPT VAR\n");
		if (var != null) var.printMe();
		if (subscript != null) subscript.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "SUBSCRIPT\nVAR\n...[...]");

		if (var       != null) AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
		if (subscript != null) AstGraphviz.getInstance().logEdge(serialNumber,subscript.serialNumber);
	}

	public Type semantMe() throws SemanticException
	{
		Type t = null;
		Type subscriptType = null;

		// check array type
		if (var != null) t = var.semantMe();

		if (t == null)
		{
			throw new SemanticException("variable has no type", lineNumber);
		}

		if (!t.isArray())
		{
			throw new SemanticException("cannot subscript non-array variable", lineNumber);
		}

		// check subscript
		if (subscript != null)
		{
			subscriptType = subscript.semantMe();
		}

		if (subscriptType != TypeInt.getInstance())
		{
			throw new SemanticException("array subscript must be of type int", lineNumber);
		}

		// negative check
		Integer constantSubscript = subscript.tryEvaluateConstant();
		if (constantSubscript != null && constantSubscript < 0)
		{
			throw new SemanticException("array subscript must be >= 0", lineNumber);
		}

		// return type
		TypeArray arrayType = (TypeArray) t;
		return arrayType.elementType;
	}

	public temp.Temp irMe()
	{
		temp.Temp dst = temp.TempFactory.getInstance().getFreshTemp();
		temp.Temp arrayAddr = var.irMe();
		temp.Temp indexTemp = subscript.irMe();
		ir.Ir.getInstance().AddIrCommand(new ir.IrCommandCheckNull(arrayAddr));
		ir.Ir.getInstance().AddIrCommand(new ir.IrCommandCheckBounds(arrayAddr, indexTemp));
		ir.Ir.getInstance().AddIrCommand(new ir.IrCommandArrayGet(dst, arrayAddr, indexTemp));
		return dst;
	}
}
