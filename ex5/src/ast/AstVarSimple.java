package ast;

import ir.*;
import temp.*;
import types.*;
import symboltable.*;

public class AstVarSimple extends AstVar
{
	/************************/
	/* simple variable name */
	/************************/
	public String name;
	
	/*************************************************/
	/* The scope offset captured during semantic     */
	/* analysis for use in IR generation             */
	/*************************************************/
	private int scopeOffset = -1;
	public boolean isGlobal = false;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstVarSimple(String name, int lineNumber)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		// System.out.format("====================== var -> ID( %s )\n",name);

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.name = name;
		this.lineNumber = lineNumber;
	}

	/**************************************************/
	/* The printing message for a simple var AST node */
	/**************************************************/
	public void printMe()
	{
		/**********************************/
		/* AST NODE TYPE = AST SIMPLE VAR */
		/**********************************/
		System.out.format("AST NODE SIMPLE VAR( %s )\n",name);

		/*********************************/
		/* Print to AST GRAPHVIZ DOT file */
		/*********************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			String.format("SIMPLE\nVAR\n(%s)",name));
	}

	/*************************************************/
	/* Additional state for implicit fields (this.*) */
	/*************************************************/
	public boolean isImplicitField = false;
	public types.TypeClass enclosingClass = null;

	/********************************************************/
	/* Semantic analysis for simple variable               */
	/* Looks up the variable name in the symbol table      */
	/********************************************************/
	public Type semantMe() throws SemanticException
	{
		Type t = SymbolTable.getInstance().find(name);

		if (t == null)
		{
			throw new SemanticException("undefined variable " + name, lineNumber);
		}
		
		/*************************************************/
		/* Capture the scope offset while scope is active */
		/*************************************************/
		this.scopeOffset = SymbolTable.getInstance().getScopeOffset(name);
		
		SymbolTableEntry entry = SymbolTable.getInstance().findEntry(name);
		if (entry != null) { // If it has no SCOPE-BOUNDARY below it, it's global
			boolean global = true;
			for (SymbolTableEntry e = entry; e != null; e = e.prevtop) {
				if ("SCOPE-BOUNDARY".equals(e.name)) {
					global = false;
					break;
				}
			}
			this.isGlobal = global;
		}

		// If it's a field, it must be an implicit access to 'this'
		if (t instanceof TypeField)
		{
			this.isImplicitField = true;
			Type thisType = SymbolTable.getInstance().find("this");
			if (thisType instanceof TypeClass) {
				this.enclosingClass = (TypeClass) thisType;
			}
			return ((TypeField) t).fieldType;
		}

		return t;
	}

	/********************************************************/
	/* IR generation for simple variable                   */
	/* Loads the variable value into a fresh temp          */
	/********************************************************/
	public Temp irMe()
	{
		Temp dst = TempFactory.getInstance().getFreshTemp();

		if (isImplicitField && enclosingClass != null) {
			// 1. Load implicit "this" pointer from stack
			Temp thisTemp = TempFactory.getInstance().getFreshTemp();
			ir.VarId.Kind kind = ir.FunctionContext.getCurrent().getKind("this");
			int fpOffset = ir.FunctionContext.getCurrent().getFpOffset("this");
			Ir.getInstance().AddIrCommand(new IrCommandLoad(thisTemp, "this", -1, kind, fpOffset));

			// 2. Load field from "this" object
			int fieldOffset = ir.ClassLayout.getFieldOffset(enclosingClass, name);
			Ir.getInstance().AddIrCommand(new IrCommandFieldGet(dst, thisTemp, fieldOffset));
		} else if (ir.FunctionContext.isInFunction()) {
			ir.VarId.Kind kind = ir.FunctionContext.getCurrent().getKind(name);
			int fpOffset = ir.FunctionContext.getCurrent().getFpOffset(name);
			Ir.getInstance().AddIrCommand(new IrCommandLoad(dst, name, scopeOffset, kind, fpOffset));
		} else {
			Ir.getInstance().AddIrCommand(new IrCommandLoad(dst, name, scopeOffset, true));
		}
		return dst;
	}
	
	public int getScopeOffset()
	{
		return scopeOffset;
	}
}
