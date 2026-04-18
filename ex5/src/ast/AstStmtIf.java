package ast;

import ir.*;
import temp.*;
import types.*;
import symboltable.*;

public class AstStmtIf extends AstStmt {
	public AstExp cond;
	public AstStmtList body;

	// constructor
	public AstStmtIf(AstExp cond, AstStmtList body, int lineNumber) {
		serialNumber = AstNode.getFreshSerialNumber();
		this.cond = cond;
		this.body = body;
		this.lineNumber = lineNumber;
	}

	public Type semantMe() throws SemanticException {
		// check if condition is int
		if (cond.semantMe() != TypeInt.getInstance()) {
			throw new SemanticException("condition inside IF is not integral", lineNumber);
		}

		// semant body in new scope
		SymbolTable.getInstance().beginScope();

		if (body != null)
			body.semantMe();

		SymbolTable.getInstance().endScope();

		return null;
	}

	public Temp irMe() {
		// allocate end label
		String labelEnd = IrCommand.getFreshLabel("end");

		// cond ir
		Temp condTemp = cond.irMe();

		// skip body if false
		Ir.getInstance().AddIrCommand(new IrCommandJumpIfEqToZero(condTemp, labelEnd));

		// body ir
		if (body != null)
			body.irMe();

		// end label
		Ir.getInstance().AddIrCommand(new IrCommandLabel(labelEnd));

		// return null
		return null;
	}
}