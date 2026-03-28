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
	public boolean isField = false;
	public TypeClass fieldOwnerClass = null;
	
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

		this.type = t;
		if (t instanceof TypeField)
		{
			this.isField = true;
			this.type = ((TypeField) t).fieldType;
			Type thisType = SymbolTable.getInstance().find("this");
			if (thisType instanceof TypeClass) {
			    this.fieldOwnerClass = (TypeClass) thisType;
			}
		}

		return this.type;
	}

	/********************************************************/
	/* IR generation for simple variable                   */
	/* Loads the variable value into a fresh temp          */
	/********************************************************/
	public Temp irMe()
	{
		Temp dst = TempFactory.getInstance().getFreshTemp();
		if (isField) {
		    Temp thisTemp = TempFactory.getInstance().getFreshTemp();
		    int thisOffset = SymbolTable.getInstance().getScopeOffset("this");
		    Ir.getInstance().AddIrCommand(new IrCommandLoad(thisTemp, "this", thisOffset, false));
		    Ir.getInstance().AddIrCommand(new IrCommandCheckNull(thisTemp));
		    int fieldOffset = types.TypeUtils.getFieldOffset(fieldOwnerClass, name);
		    Ir.getInstance().AddIrCommand(new IrCommandFieldGet(dst, thisTemp, fieldOffset));
		} else {
		    /****************************************/
		    /* Use the captured scope offset       */
		    /****************************************/
		    if (scopeOffset == -1)
		    {
		        // Fallback if semantMe wasn't called or failed (shouldn't happen in valid flow)
		        scopeOffset = SymbolTable.getInstance().getScopeOffset(name);
		    }
		    
		    Ir.getInstance().AddIrCommand(new IrCommandLoad(dst, name, scopeOffset, isGlobal));
		}
		return dst;
	}
	
	public int getScopeOffset()
	{
		return scopeOffset;
	}
}
