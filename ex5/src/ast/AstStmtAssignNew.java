package ast;

import types.*;
import ir.*;
import temp.*;

public class AstStmtAssignNew extends AstStmt
{
	// var := newExp
	public AstVar var;
	public AstExpNew newExp;

	// constructor
	public AstStmtAssignNew(AstVar var, AstExpNew newExp, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		// System.out.print("====================== stmt -> var ASSIGN newExp SEMICOLON\n");
		this.var = var;
		this.newExp = newExp;
		this.lineNumber = lineNumber;
	}

	// debug print
	public void printMe()
	{
		System.out.print("AST NODE ASSIGN NEW STMT\n");

		if (var != null) var.printMe();
		if (newExp != null) newExp.printMe();

		AstGraphviz.getInstance().logNode(serialNumber, "ASSIGN\nleft := new ...");

		if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
		if (newExp != null) AstGraphviz.getInstance().logEdge(serialNumber, newExp.serialNumber);
	}

	// semantic analysis
	public Type semantMe() throws SemanticException
	{
		Type varType = null;
		Type newExpType = null;

		// semant both sides
		if (var != null) varType = var.semantMe();
		if (newExp != null) newExpType = newExp.semantMe();

		// check for nulls
		if (varType == null)
		{
			throw new SemanticException("variable has no type", lineNumber);
		}
		if (newExpType == null)
		{
			throw new SemanticException("new expression has no type", lineNumber);
		}

		// array allocation rules
		if (newExp.exp != null && newExpType.isArray())
		{
			// This is array allocation: new T[size]
			// varType must be an array type
			if (!varType.isArray())
			{
				throw new SemanticException("cannot assign array to non-array variable", lineNumber);
			}

			TypeArray newArrayType = (TypeArray) newExpType;
			TypeArray varArrayType = (TypeArray) varType;

			// Element types must match exactly (no subclass substitution for arrays)
			if (newArrayType.elementType != varArrayType.elementType)
			{
				throw new SemanticException("array element type mismatch in assignment", lineNumber);
			}
		}
		// class or other allocation
		else
		{
			if (!TypeUtils.canAssignType(varType, newExpType))
			{
				throw new SemanticException("type mismatch in assignment: cannot assign " + newExpType.name + " to " + varType.name, lineNumber);
			}
		}

		return null;
	}

	public Temp irMe()
	{
		if (newExp != null)
		{
			if (var instanceof AstVarSimple)
			{
				AstVarSimple v = (AstVarSimple) var;
				if (v.isField) {
				    Temp thisTemp = TempFactory.getInstance().getFreshTemp();
				    Ir.getInstance().AddIrCommand(new IrCommandLoad(thisTemp, "this", v.thisScopeOffset, false));
				    Ir.getInstance().AddIrCommand(new IrCommandCheckNull(thisTemp));
				    int fieldOffset = types.TypeUtils.getFieldOffset(v.fieldOwnerClass, v.name);
				    Temp src = newExp.irMe();
				    Ir.getInstance().AddIrCommand(new IrCommandFieldSet(thisTemp, fieldOffset, src));
				} else {
				    Temp src = newExp.irMe();
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
				Temp src = newExp.irMe();
				Ir.getInstance().AddIrCommand(new IrCommandFieldSet(objAddr, offset, src));
			}
			else if (var instanceof AstVarSubscript)
			{
				AstVarSubscript sub = (AstVarSubscript) var;
				Temp arrayAddr = sub.var.irMe();
				Temp indexTemp = sub.subscript.irMe();
				Ir.getInstance().AddIrCommand(new IrCommandCheckNull(arrayAddr));
				Ir.getInstance().AddIrCommand(new IrCommandCheckBounds(arrayAddr, indexTemp));
				Temp src = newExp.irMe();
				Ir.getInstance().AddIrCommand(new IrCommandArraySet(arrayAddr, indexTemp, src));
			}
		}
		return null;
	}
}
