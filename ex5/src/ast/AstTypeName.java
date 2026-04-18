package ast;

import types.*;
import symboltable.*;

public class AstTypeName extends AstNode
{
	// members
	public String type;
	public String name;
	
	// constructor
	public AstTypeName(String type, String name)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
	
		this.type = type;
		this.name = name;
	}

	public void printMe()
	{
		System.out.format("NAME(%s):TYPE(%s)\n",name,type);
		AstGraphviz.getInstance().logNode(serialNumber, String.format("NAME:TYPE\n%s:%s",name,type));
	}

	public Type semantMe() throws SemanticException
	{
		Type t = SymbolTable.getInstance().find(type);
		if (t == null)
		{
			throw new SemanticException("undeclared type " + type, lineNumber);
		}
		
		SymbolTable.getInstance().enter(name, t);
		return t;
	}	
}
