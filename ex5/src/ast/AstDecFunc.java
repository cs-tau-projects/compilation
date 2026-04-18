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

    // Param scope offsets captured during semantMe() (while scope is open)
    private java.util.List<Integer> paramScopeOffsets = new java.util.ArrayList<>();

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

	// semant with registration option
	public Type semantMe(boolean isMethod) throws SemanticException
	{
		Type paramType;
		Type retType = null;
		TypeList paramTypeList = null;

		// Check for reserved keywords
		TypeUtils.checkNotReservedKeyword(funcName, lineNumber);

		// Validate return type
		retType = SymbolTable.getInstance().find(this.returnType.typeName);
		if (retType == null)
		{
			throw new SemanticException("non existing return type " + this.returnType.typeName, lineNumber);
		}

		// Check for function name collision (standalone functions only)
		if (!isMethod && SymbolTable.getInstance().find(funcName) != null)
		{
			throw new SemanticException("function " + funcName + " already exists", lineNumber);
		}

		// Build parameter type list
		paramTypeList = TypeUtils.buildParameterTypeList(params, lineNumber);

		// Register function type before opening scope to support recursion
		TypeFunction funcType = new TypeFunction(retType, funcName, paramTypeList);
		if (!isMethod)
		{
			SymbolTable.getInstance().enter(funcName, funcType);
		}

		SymbolTable.getInstance().beginScope();
		paramScopeOffsets.clear();

		if (isMethod) {
			Type classType = SymbolTable.getInstance().find(this.className);
			SymbolTable.getInstance().enter("this", classType);
			paramScopeOffsets.add(SymbolTable.getInstance().getScopeOffset("this"));
		}

		// Set current function return type for return statement validation
		SymbolTable.getInstance().setCurrentFunctionReturnType(retType);

		// Enter parameters into symbol table and capture their scope offsets
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
			// Capture the scope offset now while the symbol table entry exists
			paramScopeOffsets.add(SymbolTable.getInstance().getScopeOffset(it.head.id));
		}

		// Semantic analysis of the function body
		if (body != null)
		{
			body.semantMe();
		}

		// Finalize scope and clear return type focus
		SymbolTable.getInstance().endScope();
		SymbolTable.getInstance().setCurrentFunctionReturnType(null);

		// Return null as function declarations don't have a value type in this context
		return null;
	}

	public static String getFuncLabel(String funcName, String className) {
	    if (funcName.equals("main")) return "user_main";
	    if (className != null) return "Method_" + className + "_" + funcName;
	    return "func_" + funcName;
	}

	public Temp irMe()
	{
	    String emitName = getFuncLabel(funcName, className);
		Ir.getInstance().AddIrCommand(new IrCommandLabel(emitName));
		Ir.getInstance().AddIrCommand(new IrCommandFuncPrologue());
		
		int numArgs = paramScopeOffsets.size();
		
		// Use the scope offsets captured during semantMe() (scope is closed now)
		for (int i = 0; i < paramScopeOffsets.size(); i++) {
		    int paramScopeOffset = paramScopeOffsets.get(i);
		    String paramName = (className != null && i == 0) ? "this" : null;
		    if (paramName == null) {
		        AstParametersList p = params;
		        int pIdx = (className != null) ? i - 1 : i;
		        for (int k = 0; k < pIdx; k++) p = p.tail;
		        paramName = p.head.id;
		    }
		    Ir.getInstance().AddIrCommand(new IrCommandAllocateParam(paramName, paramScopeOffset, i, numArgs));
		}

		if (body != null) body.irMe();

        Temp dst = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IRcommandConstInt(dst, 0));
        Ir.getInstance().AddIrCommand(new IrCommandReturn(dst));

		return null;
	}
}