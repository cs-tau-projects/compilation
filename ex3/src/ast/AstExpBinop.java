package ast;
import types.*;
import symboltable.*;

public class AstExpBinop extends AstExp
{
	BinOp op;
	public AstExp left;
	public AstExp right;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstExpBinop(AstExp left, AstExp right, BinOp op, int lineNumber)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		// System.out.print("====================== exp -> exp BINOP exp\n");

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.left = left;
		this.right = right;
		this.op = op;
		this.lineNumber = lineNumber;
	}
	
	/*************************************************/
	/* The printing message for a binop exp AST node */
	/*************************************************/
	public void printMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST BINOP EXP */
		/*************************************/
		System.out.print("AST NODE BINOP EXP\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (left != null) left.printMe();
		if (right != null) right.printMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			String.format("BINOP(%s)", op.toString()));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (left  != null) AstGraphviz.getInstance().logEdge(serialNumber,left.serialNumber);
		if (right != null) AstGraphviz.getInstance().logEdge(serialNumber,right.serialNumber);
	}

	public Type semantMe() throws SemanticException
	{
		Type t1 = null;
		Type t2 = null;

		/****************************/
		/* [1] Semant both operands */
		/****************************/
		if (left  != null) t1 = left.semantMe();
		if (right != null) t2 = right.semantMe();

		/****************************/
		/* [2] Check for null types */
		/****************************/
		if (t1 == null || t2 == null)
		{
			throw new SemanticException("operand has no type", lineNumber);
		}

		/******************************************/
		/* [3] Handle different operator types    */
		/******************************************/

		switch (op)
		{
			case PLUS:
				// Plus operator: + can be used for int+int OR string+string
				if (t1 == TypeInt.getInstance() && t2 == TypeInt.getInstance())
				{
					return TypeInt.getInstance();
				}
				else if (t1 == TypeString.getInstance() && t2 == TypeString.getInstance())
				{
					return TypeString.getInstance();
				}
				else
				{
					throw new SemanticException("+ operator requires both operands to be int or both to be string", lineNumber);
				}

			case MINUS:
			case TIMES:
			case DIVIDE:
				// Arithmetic operators: -, *, /
				// Both operands must be int, returns int
				if (t1 != TypeInt.getInstance() || t2 != TypeInt.getInstance())
				{
					throw new SemanticException("arithmetic operator requires int operands", lineNumber);
				}

				// Division by zero check for constant expressions
				if (op == BinOp.DIVIDE && right instanceof AstExpInt)
				{
					AstExpInt rightInt = (AstExpInt) right;
					if (rightInt.value == 0)
					{
						throw new SemanticException("division by zero", lineNumber);
					}
				}

				return TypeInt.getInstance();

			case LT:
			case GT:
				// Comparison operators: <, >
				// Both operands must be int, returns int (0 or 1)
				if (t1 != TypeInt.getInstance() || t2 != TypeInt.getInstance())
				{
					throw new SemanticException("comparison operator requires int operands", lineNumber);
				}
				return TypeInt.getInstance();

			case EQ:
				// Equality operator: =
				// Both operands must be compatible types, returns int (0 or 1)

				// Check if types are compatible for equality comparison
				if (t1 == t2)
				{
					// Same type - always valid
					return TypeInt.getInstance();
				}

				// Check if one is nil and the other is class/array
				if (t1.name != null && t1.name.equals("nil"))
				{
					if (t2.isClass() || t2.isArray())
					{
						return TypeInt.getInstance();
					}
				}
				if (t2.name != null && t2.name.equals("nil"))
				{
					if (t1.isClass() || t1.isArray())
					{
						return TypeInt.getInstance();
					}
				}

				// Check if both are classes and one is subclass of the other
				if (t1.isClass() && t2.isClass())
				{
					TypeClass c1 = (TypeClass) t1;
					TypeClass c2 = (TypeClass) t2;
					if (TypeUtils.isSubclassOf(c1, c2) || TypeUtils.isSubclassOf(c2, c1))
					{
						return TypeInt.getInstance();
					}
				}

				// Types are not compatible for equality
				throw new SemanticException("incompatible types for equality comparison", lineNumber);

			default:
				throw new SemanticException("unknown binary operator", lineNumber);
		}
	}
}
