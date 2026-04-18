package ast;

import ir.*;
import temp.*;
import types.*;
import symboltable.*;

public class AstStmtIf extends AstStmt
{
	public AstExp cond;
	public AstStmtList body;

	// constructor
	public AstStmtIf(AstExp cond, AstStmtList body, int lineNumber)
	{
		serialNumber = AstNode.getFreshSerialNumber();
		this.cond = cond;
		this.body = body;
		this.lineNumber = lineNumber;
	}

	public Type semantMe() throws SemanticException
	{
		// check condition
		if (cond.semantMe() != TypeInt.getInstance())
		{
			throw new SemanticException("condition inside IF is not integral", lineNumber);
		}
		
		// begin scope
		SymbolTable.getInstance().beginScope();

		// semant body
		if (body != null) body.semantMe();

		// end scope
		SymbolTable.getInstance().endScope();

		// return nothing
		return null;
	}

	public Temp irMe()
	{
		// label for end
		String labelEnd = IrCommand.getFreshLabel("end");

		// ir cond
		Temp condTemp = cond.irMe();

		// jump to end if false
		Ir.
				getInstance().
				AddIrCommand(new IrCommandJumpIfEqToZero(condTemp, labelEnd));

		// ir body
		if (body != null) body.irMe();

		// end label
		Ir.
				getInstance().
				AddIrCommand(new IrCommandLabel(labelEnd));

		// done
		return null;
	}
}