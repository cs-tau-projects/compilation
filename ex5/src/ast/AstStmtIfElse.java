package ast;

import ir.*;
import temp.*;
import types.*;
import symboltable.*;

public class AstStmtIfElse extends AstStmt
{
	public AstExp cond;
	public AstStmtList ifBody;
	public AstStmtList elseBody;

	// constructor
	public AstStmtIfElse(AstExp cond, AstStmtList ifBody, AstStmtList elseBody, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		// System.out.print("====================== stmt -> IF LPAREN exp RPAREN LBRACE stmtList RBRACE ELSE LBRACE stmtList RBRACE\n");
		this.cond = cond;
		this.ifBody = ifBody;
		this.elseBody = elseBody;
		this.lineNumber = lineNumber;
	}

	// print
	public void printMe()
	{
		System.out.print("AST NODE IF-ELSE STMT\n");

		if (cond != null) cond.printMe();
		if (ifBody != null) ifBody.printMe();
		if (elseBody != null) elseBody.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "IF-ELSE");
		
		if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
		if (ifBody != null) AstGraphviz.getInstance().logEdge(serialNumber, ifBody.serialNumber);
		if (elseBody != null) AstGraphviz.getInstance().logEdge(serialNumber, elseBody.serialNumber);
	}

	// semant
	public Type semantMe() throws SemanticException
	{
		// check condition type
		if (cond != null)
		{
			Type condType = cond.semantMe();

			if (condType != TypeInt.getInstance())
			{
				throw new SemanticException("condition inside IF is not integral", lineNumber);
			}
		}

		// analyze if branch
		SymbolTable.getInstance().beginScope();

		if (ifBody != null)
		{
			ifBody.semantMe();
		}

		SymbolTable.getInstance().endScope();

		// analyze else branch
		SymbolTable.getInstance().beginScope();

		if (elseBody != null)
		{
			elseBody.semantMe();
		}

		SymbolTable.getInstance().endScope();

		return null;
	}

	public Temp irMe()
	{
		// labels
		String labelElse = IrCommand.getFreshLabel("else");
		String labelEnd = IrCommand.getFreshLabel("end");

		// ir cond
		Temp condTemp = cond.irMe();

		// jump to else if false
		Ir.
				getInstance().
				AddIrCommand(new IrCommandJumpIfEqToZero(condTemp, labelElse));

		// ir if-body
		if (ifBody != null) ifBody.irMe();

		// skip else
		Ir.
				getInstance().
				AddIrCommand(new IrCommandJumpLabel(labelEnd));

		// else entry
		Ir.
				getInstance().
				AddIrCommand(new IrCommandLabel(labelElse));

		// ir else-body
		if (elseBody != null) elseBody.irMe();

		// end label
		Ir.
				getInstance().
				AddIrCommand(new IrCommandLabel(labelEnd));

		// done
		return null;
	}
}
