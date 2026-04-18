package ast;

import types.*;
import symboltable.*;
import ir.*;
import temp.*;

public class AstStmtReturn extends AstStmt
{
	public AstExp exp;  // can be null for void return

	// constructor
	public AstStmtReturn(AstExp exp, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		// System.out.print("====================== stmt -> RETURN exp SEMICOLON\n");
		this.exp = exp;
		this.lineNumber = lineNumber;
	}

	// print
	public void printMe()
	{
		System.out.print("AST NODE RETURN STMT\n");

		if (exp != null) exp.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "RETURN");

		if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
	}

	// semant
	public Type semantMe() throws SemanticException
	{
		Type expectedReturnType = SymbolTable.getInstance().getCurrentFunctionReturnType();

		// scope check
		if (expectedReturnType == null)
		{
			throw new SemanticException("return statement outside of function", lineNumber);
		}

		// void check
		if (expectedReturnType instanceof TypeVoid)
		{
			if (exp != null)
			{
				throw new SemanticException("void function cannot return a value", lineNumber);
			}
			return null;
		}

		// non-void check
		if (exp == null)
		{
			throw new SemanticException("non-void function must return a value", lineNumber);
		}

		// type match
		Type actualReturnType = exp.semantMe();

		if (!TypeUtils.canAssignType(expectedReturnType, actualReturnType))
		{
			throw new SemanticException("return type mismatch", lineNumber);
		}

		return null;
	}

	public Temp irMe()
	{
		Temp retVal = null;
		if (exp != null)
		{
			retVal = exp.irMe();
		} else {
		    retVal = TempFactory.getInstance().getFreshTemp();
		    Ir.getInstance().AddIrCommand(new IRcommandConstInt(retVal, 0));
		}
		
		Ir.getInstance().AddIrCommand(new IrCommandReturn(retVal));
		return null;
	}
}
