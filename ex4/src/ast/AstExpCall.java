package ast;

import ir.*;
import symboltable.*;
import temp.*;
import types.*;

public class AstExpCall extends AstExp
{
	public AstVar var;  // can be null for simple function calls
	public String funcName;
	public AstExpList params;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	// Constructor for simple function call: funcName(params)
	public AstExpCall(String funcName, AstExpList params, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.var = null;
		this.funcName = funcName;
		this.params = params;
		this.lineNumber = lineNumber;
	}

	// Constructor for method call: var.funcName(params)
	public AstExpCall(AstVar var, String funcName, AstExpList params, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.var = var;
		this.funcName = funcName;
		this.params = params;
		this.lineNumber = lineNumber;
	}

	/*************************************************/
	/* The printing message for a call exp AST node */
	/*************************************************/
	@Override
	public void printMe()
	{
		System.out.print("AST NODE CALL EXP\n");

		if (var != null) var.printMe();
		System.out.format("FUNC NAME( %s )\n", funcName);
		if (params != null) params.printMe();

		String label = (var != null) ? 
			String.format("CALL\n%s.%s(...)", "var", funcName) :
			String.format("CALL\n%s(...)", funcName);

		AstGraphviz.getInstance().logNode(serialNumber, label);
		
		if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
		if (params != null) AstGraphviz.getInstance().logEdge(serialNumber, params.serialNumber);
	}

	/********************************************************/
	/* Semantic analysis for function/method call          */
	/* Handles: funcName(params) - function call           */
	/*          var.funcName(params) - method call         */
	/********************************************************/
	@Override
	public Type semantMe() throws SemanticException
	{
		Type funcType = null;
		TypeFunction func = null;

		/************************************************/
		/* [1] Handle method call: var.funcName(params) */
		/************************************************/
		if (var != null)
		{
			// Method call - get the type of var
			Type varType = var.semantMe();

			if (varType == null)
			{
				throw new SemanticException("variable has no type", lineNumber);
			}

			// var must be a class
			if (!varType.isClass())
			{
				throw new SemanticException("cannot call method on non-class type", lineNumber);
			}

			TypeClass classType = (TypeClass) varType;

			// Look for method in class hierarchy
			funcType = TypeUtils.findMemberInClassHierarchy(classType, funcName);

			if (funcType == null)
			{
				throw new SemanticException("method " + funcName + " does not exist in class " + classType.name, lineNumber);
			}

			// Make sure it's a function, not a field
			if (!(funcType instanceof TypeFunction))
			{
				throw new SemanticException(funcName + " is not a method", lineNumber);
			}

			func = (TypeFunction) funcType;
		}
		/************************************************/
		/* [2] Handle function call: funcName(params)   */
		/************************************************/
		else
		{
			// Simple function call - look up in symbol table
			funcType = SymbolTable.getInstance().find(funcName);

			if (funcType == null)
			{
				throw new SemanticException("undefined function " + funcName, lineNumber);
			}

			if (!(funcType instanceof TypeFunction))
			{
				throw new SemanticException(funcName + " is not a function", lineNumber);
			}

			func = (TypeFunction) funcType;
		}

		/************************************************/
		/* [3] Check parameter types match              */
		/************************************************/
		checkParameterTypes(func.params, params, funcName);

		/************************************************/
		/* [4] Return the function's return type        */
		/************************************************/
		return func.returnType;
	}

	/******************************************************************/
	/* Helper: Check that actual parameters match expected types     */
	/******************************************************************/
	private void checkParameterTypes(TypeList expectedParams, AstExpList actualParams, String functionName) throws SemanticException
	{
		TypeList expected = expectedParams;
		AstExpList actual = actualParams;

		while (expected != null || actual != null)
		{
			// Different number of parameters
			if (expected == null || actual == null)
			{
				throw new SemanticException("wrong number of arguments to function " + functionName, lineNumber);
			}

			// Check parameter type
			Type actualType = actual.head.semantMe();
			Type expectedType = expected.head;

			if (!TypeUtils.canAssignType(expectedType, actualType))
			{
				throw new SemanticException("parameter type mismatch in call to " + functionName, lineNumber);
			}

			expected = expected.tail;
			actual = actual.tail;
		}
	}

	@Override
	public Temp irMe()
	{
		// Handle libarary functions
		if (funcName.equals("PrintInt") && params != null){
			Temp t = params.head.irMe();
			Ir.getInstance().AddIrCommand(new IrCommandPrintInt(t));
			return null;
		}

		else if (funcName.equals("PrintString") && params != null){
			Temp t = params.head.irMe();
			Ir.getInstance().AddIrCommand(new IrCommandPrintString(t));
			return null;
		}
	}
}

