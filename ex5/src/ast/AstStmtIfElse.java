package ast;

import ir.*;
import temp.*;
import types.*;
import symboltable.*;

public class AstStmtIfElse extends AstStmt {
	public AstExp cond;
	public AstStmtList ifBody;
	public AstStmtList elseBody;

	// constructor
	public AstStmtIfElse(AstExp cond, AstStmtList ifBody, AstStmtList elseBody, int lineNumber) {
		serialNumber = AstNodeSerialNumber.getFresh();
		// System.out.print("====================== stmt -> IF LPAREN exp RPAREN LBRACE
		// stmtList RBRACE ELSE LBRACE stmtList RBRACE\n");
		this.cond = cond;
		this.ifBody = ifBody;
		this.elseBody = elseBody;
		this.lineNumber = lineNumber;
	}

	// debug print
	public void printMe() {
		System.out.print("AST NODE IF-ELSE STMT\n");

		if (cond != null)
			cond.printMe();
		if (ifBody != null)
			ifBody.printMe();
		if (elseBody != null)
			elseBody.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "IF-ELSE");

		if (cond != null)
			AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
		if (ifBody != null)
			AstGraphviz.getInstance().logEdge(serialNumber, ifBody.serialNumber);
		if (elseBody != null)
			AstGraphviz.getInstance().logEdge(serialNumber, elseBody.serialNumber);
	}

	// semantic analysis
	public Type semantMe() throws SemanticException {
		// check condition
		if (cond != null) {
			Type condType = cond.semantMe();

			if (condType != TypeInt.getInstance()) {
				throw new SemanticException("condition inside IF is not integral", lineNumber);
			}
		}

		// if branch
		SymbolTable.getInstance().beginScope();

		if (ifBody != null) {
			ifBody.semantMe();
		}

		SymbolTable.getInstance().endScope();

		// else branch
		SymbolTable.getInstance().beginScope();

		if (elseBody != null) {
			elseBody.semantMe();
		}

		SymbolTable.getInstance().endScope();

		return null;
	}

	public Temp irMe() {

		// allocate labels
		String labelElse = IrCommand.getFreshLabel("else");
		String labelEnd = IrCommand.getFreshLabel("end");

		// cond.irMe();

		Temp condTemp = cond.irMe();

		// labels and branches
		Ir.getInstance().AddIrCommand(new IrCommandJumpIfEqToZero(condTemp, labelElse));
		if (ifBody != null)
			ifBody.irMe();
		Ir.getInstance().AddIrCommand(new IrCommandJumpLabel(labelEnd));
		Ir.getInstance().AddIrCommand(new IrCommandLabel(labelElse));
		if (elseBody != null)
			elseBody.irMe();
		Ir.getInstance().AddIrCommand(new IrCommandLabel(labelEnd));

		// return null

		return null;
	}
}
