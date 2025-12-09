
package ast;

import types.*;
import symboltable.*;

public class AstDecFunc extends AstNode
{
    public AstType returnType;
    public String funcName;
    public AstParametersList params;   
    public AstStmtList body;           
    public AstDecFunc(AstType returnType, String funcName, AstParametersList params, AstStmtList body)
    {
        serialNumber = AstNode.getFreshSerialNumber();
        this.returnType = returnType;
        this.funcName = funcName;
        this.params = params;
        this.body = body;
    }

    @Override
    public void printMe(){
        System.out.format("AST DEC FUNC NODE: %s\n", funcName);
        if (returnType != null) returnType.printMe();
        if (params != null) params.printMe();
        if (body != null) body.printMe();
    }

    public Type semantMe() throws SemanticException
	{
		Type t;
		Type retType = null;
		TypeList paramTypeList = null;

		/*******************/
		/* [0] Check if return type exists */
		/*******************/
		retType = SymbolTable.getInstance().find(this.returnType.typeName);
		if (retType == null)
		{
			throw new SemanticException("non existing return type " + this.returnType.typeName, lineNumber);
		}

		/**************************************/
		/* [1] Check if function name already exists */
		/**************************************/
		if (SymbolTable.getInstance().find(funcName) != null)
		{
			throw new SemanticException("function " + funcName + " already exists", lineNumber);
		}

		/****************************/
		/* [2] Begin Function Scope */
		/****************************/
		SymbolTable.getInstance().beginScope();

		/*******************************************************/
		/* [2.5] Set current function return type for return  */
		/*       statement validation                         */
		/*******************************************************/
		SymbolTable.getInstance().setCurrentFunctionReturnType(retType);

		/***************************/
		/* [3] Semant Input Params */
		/***************************/
		for (AstParametersList it = params; it != null; it = it.tail)
		{
			t = SymbolTable.getInstance().find(it.head.type.typeName);
			if (t == null)
			{
				throw new SemanticException("non existing type " + it.head.type.typeName, lineNumber);
			}

			// Check if parameter name already exists in current scope
			if (SymbolTable.getInstance().find(it.head.id) != null)
			{
				throw new SemanticException("parameter " + it.head.id + " already exists in scope", lineNumber);
			}

			paramTypeList = new TypeList(t, paramTypeList);
			SymbolTable.getInstance().enter(it.head.id, t);
		}

		/*******************/
		/* [4] Semant Body */
		/*******************/
		if (body != null)
		{
			body.semantMe();
		}

		/*****************/
		/* [5] End Scope */
		/*****************/
		SymbolTable.getInstance().endScope();

		/*******************************************************/
		/* [5.5] Clear current function return type           */
		/*******************************************************/
		SymbolTable.getInstance().setCurrentFunctionReturnType(null);

		/***************************************************/
		/* [6] Enter the Function Type to the Symbol Table */
		/***************************************************/
		SymbolTable.getInstance().enter(funcName, new TypeFunction(retType, funcName, paramTypeList));

		/************************************************************/
		/* [7] Return value is irrelevant for function declarations */
		/************************************************************/
		return null;
	}
}