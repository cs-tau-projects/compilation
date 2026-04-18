package ast;

import ir.*;
import temp.*;
import types.*;
import symboltable.*;

public class AstStmtWhile extends AstStmt {
	public AstExp cond;
	public AstStmtList body;

	// constructor
	public AstStmtWhile(AstExp cond, AstStmtList body, int lineNumber) {
		this.cond = cond;
		this.body = body;
		this.lineNumber = lineNumber;
	}

	// semant
	public Type semantMe() throws SemanticException {
		// check condition type
		if (cond != null) {
			Type condType = cond.semantMe();

			if (condType != TypeInt.getInstance()) {
				throw new SemanticException("condition inside WHILE is not integral", lineNumber);
			}
		}

		// analyze body in new scope
		SymbolTable.getInstance().beginScope();

		if (body != null) {
			body.semantMe();
		}

		SymbolTable.getInstance().endScope();

		return null;
	}

	public Temp irMe() {
		// labels for loop
		String labelEnd = IrCommand.getFreshLabel("end");
		String labelStart = IrCommand.getFreshLabel("start");

		// entry label
		Ir.getInstance().AddIrCommand(new IrCommandLabel(labelStart));

		// ir cond
		Temp condTemp = cond.irMe();

		// jump to end if false
		Ir.getInstance().AddIrCommand(new IrCommandJumpIfEqToZero(condTemp, labelEnd));

		// ir body
		if (body != null)
			body.irMe();

		// jump back to start
		Ir.getInstance().AddIrCommand(new IrCommandJumpLabel(labelStart));

		// loop end label
		Ir.getInstance().AddIrCommand(new IrCommandLabel(labelEnd));

		// done
		return null;
	}
}