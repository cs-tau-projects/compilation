package ast;

import ir.*;
import temp.*;
import types.*;
import symboltable.*;

public class AstStmtAssign extends AstStmt
{
	// var := exp
	public AstVar var;
	public AstExp exp;

	// constructor
	public AstStmtAssign(AstVar var, AstExp exp, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.var = var;
		this.exp = exp;
		this.lineNumber = lineNumber;
	}

	// debug print
	public void printMe()
	{
		System.out.print("AST NODE ASSIGN STMT\n");
		if (var != null) var.printMe();
		if (exp != null) exp.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "ASSIGN\nleft := right\n");
		
		AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
		AstGraphviz.getInstance().logEdge(serialNumber,exp.serialNumber);
	}

	public Type semantMe() throws SemanticException
	{
		Type t1 = null;
		Type t2 = null;

		// check types of both sides
		if (var != null) t1 = var.semantMe();
		if (exp != null) t2 = exp.semantMe();

		// validate existence
		if (t1 == null)
		{
			throw new SemanticException("variable has no type", lineNumber);
		}
		if (t2 == null)
		{
			throw new SemanticException("expression has no type", lineNumber);
		}

		// check compatibility
		if (!TypeUtils.canAssignType(t1, t2))
		{
			throw new SemanticException("type mismatch in assignment: cannot assign " + t2.name + " to " + t1.name, lineNumber);
		}
		return null;
	}

	public Temp irMe()
	{
		if (var instanceof AstVarSimple)
		{
			AstVarSimple v = (AstVarSimple) var;
			if (v.isField) {
			    Temp thisTemp = TempFactory.getInstance().getFreshTemp();
			    Ir.getInstance().AddIrCommand(new IrCommandLoad(thisTemp, "this", v.thisScopeOffset, false));
			    Ir.getInstance().AddIrCommand(new IrCommandCheckNull(thisTemp));
			    int fieldOffset = types.TypeUtils.getFieldOffset(v.fieldOwnerClass, v.name);
			    Temp src = exp.irMe();
			    Ir.getInstance().AddIrCommand(new IrCommandFieldSet(thisTemp, fieldOffset, src));
			} else {
			    Temp src = exp.irMe();
			    String varName = v.name;
			    int scopeOffset = v.getScopeOffset();
			    boolean isGlobal = v.isGlobal;
			    Ir.getInstance().AddIrCommand(new IrCommandStore(varName, scopeOffset, src, isGlobal));
			}
		}
		else if (var instanceof AstVarField)
		{
			AstVarField f = (AstVarField) var;
			Temp objAddr = f.var.irMe();
			Ir.getInstance().AddIrCommand(new IrCommandCheckNull(objAddr));
			int offset = types.TypeUtils.getFieldOffset(f.ownerClass, f.fieldName);
			Temp src = exp.irMe();
			Ir.getInstance().AddIrCommand(new IrCommandFieldSet(objAddr, offset, src));
		}
		else if (var instanceof AstVarSubscript)
		{
			AstVarSubscript sub = (AstVarSubscript) var;
			Temp arrayAddr = sub.var.irMe();
			Temp indexTemp = sub.subscript.irMe();
			Ir.getInstance().AddIrCommand(new IrCommandCheckNull(arrayAddr));
			Ir.getInstance().AddIrCommand(new IrCommandCheckBounds(arrayAddr, indexTemp));
			Temp src = exp.irMe();
			Ir.getInstance().AddIrCommand(new IrCommandArraySet(arrayAddr, indexTemp, src));
		}
		return null;
	}
}
