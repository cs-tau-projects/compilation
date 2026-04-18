package ast;

import types.*;

public class AstVarField extends AstVar
{
	public AstVar var;
	public String fieldName;
	
	// constructor
	public AstVarField(AstVar var, String fieldName, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.var = var;
		this.fieldName = fieldName;
		this.lineNumber = lineNumber;
	}

	// debug print
	public void printMe()
	{
		System.out.print("AST NODE FIELD VAR\n");
		if (var != null) var.printMe();
		System.out.format("FIELD NAME( %s )\n",fieldName);

		AstGraphviz.getInstance().logNode(
				serialNumber,
			String.format("FIELD\nVAR\n...->%s",fieldName));
		
		if (var != null) AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
	}

	// semantic analysis
	public Type semantMe() throws SemanticException
	{
		Type t = null;
		TypeClass tc = null;

		// 1. Validate variable type and ensure it is a class
		if (var != null) t = var.semantMe();

		if (t == null)
		{
			throw new SemanticException("variable has no type", lineNumber);
		}

		if (!t.isClass())
		{
			throw new SemanticException("cannot access field " + fieldName + " of non-class variable", lineNumber);
		}

		tc = (TypeClass) t;
		this.ownerClass = tc;

		// 2. Lookup field in class hierarchy and return its type
		Type member = TypeUtils.findMemberInClassHierarchy(tc, fieldName);

		if (member == null)
		{
			throw new SemanticException("field " + fieldName + " does not exist in class " + tc.name, lineNumber);
		}

		if (member instanceof TypeField)
		{
			this.type = ((TypeField) member).fieldType;
			return this.type;
		}
		this.type = member;
		return this.type;
	}

	public TypeClass ownerClass = null;

	public temp.Temp irMe()
	{
		temp.Temp dst = temp.TempFactory.getInstance().getFreshTemp();
		temp.Temp objAddr = var.irMe();
		ir.Ir.getInstance().AddIrCommand(new ir.IrCommandCheckNull(objAddr));
		int offset = types.TypeUtils.getFieldOffset(ownerClass, fieldName);
		ir.Ir.getInstance().AddIrCommand(new ir.IrCommandFieldGet(dst, objAddr, offset));
		return dst;
	}
}
