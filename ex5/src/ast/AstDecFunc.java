package ast;

import ir.*;
import temp.*;
import types.*;
import symboltable.*;

public class AstDecFunc extends AstNode
{
    public AstType returnType;
    public String funcName;
    public AstParametersList params;   
    public AstStmtList body;           
    public String className = null;           

    public AstDecFunc(AstType returnType, String funcName, AstParametersList params, AstStmtList body, int lineNumber)
    {
        serialNumber = AstNode.getFreshSerialNumber();
        this.returnType = returnType;
        this.funcName = funcName;
        this.params = params;
        this.body = body;
        this.lineNumber = lineNumber;
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
		return semantMe(false);
	}

	/******************************************************************/
	/* Semantic analysis with option to skip registration            */
	/* isMethod = true when called from AstDecClass for methods      */
	/* isMethod = false for standalone functions                     */
	/******************************************************************/
	public Type semantMe(boolean isMethod) throws SemanticException
	{
		Type paramType;
		Type retType = null;
		TypeList paramTypeList = null;

		/************************************/
		/* [0a] Check for reserved keyword  */
		/************************************/
		TypeUtils.checkNotReservedKeyword(funcName, lineNumber);

		/*******************/
		/* [0b] Check if return type exists */
		/*******************/
		retType = SymbolTable.getInstance().find(this.returnType.typeName);
		if (retType == null)
		{
			throw new SemanticException("non existing return type " + this.returnType.typeName, lineNumber);
		}

		/**************************************/
		/* [1] Check if function name already exists */
		/* (Skip this check for methods - already validated by AstDecClass) */
		/**************************************/
		if (!isMethod && SymbolTable.getInstance().find(funcName) != null)
		{
			throw new SemanticException("function " + funcName + " already exists", lineNumber);
		}

		/***************************/
		/* [2] Build parameter type list */
		/***************************/
		paramTypeList = TypeUtils.buildParameterTypeList(params, lineNumber);

		/***************************************************/
		/* [2.5] Enter the Function Type to the Symbol Table */
		/* BEFORE opening scope to allow recursive calls    */
		/* and to make function visible to later declarations */
		/* (Skip for methods - already entered by AstDecClass) */
		/***************************************************/
		TypeFunction funcType = new TypeFunction(retType, funcName, paramTypeList);
		if (!isMethod)
		{
			SymbolTable.getInstance().enter(funcName, funcType);
		}

		/****************************/
		/* [3] Begin Function Scope */
		/****************************/
		SymbolTable.getInstance().beginScope();

		/*******************************************************/
		/* [3.5] Set current function return type for return  */
		/*       statement validation                         */
		/*******************************************************/
		SymbolTable.getInstance().setCurrentFunctionReturnType(retType);

		// Enter parameters into symbol table
		for (AstParametersList it = params; it != null; it = it.tail)
		{
			// Check for reserved keyword in parameter name
			TypeUtils.checkNotReservedKeyword(it.head.id, lineNumber);

			paramType = SymbolTable.getInstance().find(it.head.type.typeName);

			// Check if parameter name already exists in current scope
			if (SymbolTable.getInstance().findInCurrentScope(it.head.id) != null)
			{
				throw new SemanticException("parameter " + it.head.id + " already exists in scope", lineNumber);
			}

			SymbolTable.getInstance().enter(it.head.id, paramType);
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

		/************************************************************/
		/* [7] Return value is irrelevant for function declarations */
		/************************************************************/
		return null;
	}

	/****************************************/
	/* Count local variable declarations   */
	/* in the function body                */
	/****************************************/
	private int countLocals(AstStmtList stmts) {
		int count = 0;
		for (AstStmtList it = stmts; it != null; it = it.tail) {
			if (it.head instanceof AstStmtVarDec) {
				count++;
			}
		}
		return count;
	}

	/****************************************/
	/* Count the number of parameters      */
	/****************************************/
	private int countParams() {
		int count = 0;
		if (className != null) count++; // 'this' pointer for methods
		for (AstParametersList it = params; it != null; it = it.tail) {
			count++;
		}
		return count;
	}

	public Temp irMe()
	{
	    String emitName = funcName;
	    if (funcName.equals("main")) {
	        emitName = "user_main";
	    } else if (className != null) {
	        emitName = "Method_" + className + "_" + funcName;
	    }

	    int numParams = countParams();
	    int numLocals = countLocals(body);

	    /****************************************/
	    /* [1] Emit function label             */
	    /****************************************/
		Ir.getInstance().AddIrCommand(new IrCommandLabel(emitName));

	    /****************************************/
	    /* [2] Set up function context          */
	    /****************************************/
		FunctionContext.enterFunction(emitName, numParams);

	    /****************************************/
	    /* [3] Register parameters in context   */
	    /* Params at +8, +12, +16... from $fp   */
	    /****************************************/
		int paramIndex = 0;
		if (className != null) {
			FunctionContext.getCurrent().addParam("this", paramIndex);
			paramIndex++;
		}
		for (AstParametersList it = params; it != null; it = it.tail) {
			FunctionContext.getCurrent().addParam(it.head.id, paramIndex);
			paramIndex++;
		}

	    /****************************************/
	    /* [4] Emit prologue with local count   */
	    /****************************************/
		Ir.getInstance().AddIrCommand(new IrCommandFuncPrologue(numLocals));

	    /****************************************/
	    /* [5] Emit body                       */
	    /****************************************/
		if (body != null) body.irMe();

	    /****************************************/
	    /* [6] Emit fallthrough return          */
	    /* (for void functions without return)  */
	    /****************************************/
        Temp dst = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IRcommandConstInt(dst, 0));
        Ir.getInstance().AddIrCommand(new IrCommandReturn(dst));

	    /****************************************/
	    /* [7] Exit function context            */
	    /****************************************/
		FunctionContext.exitFunction();

		return null;
	}
}