package ast;
import java.io.PrintWriter;

public class AstType extends AstNode
{
	public static final String INT_TYPE = "int";
	public static final String STRING_TYPE = "string";
	public static final String VOID_TYPE = "void";

	String typeName; // has to be one of the above or an identifier name

	public AstType(String typeName)
	{
		serialNumber = AstNode.getFreshSerialNumber();
		this.typeName = typeName;
	}

	@Override
	public void printMe(){
		System.out.format("AST TYPE NODE: %s\n", typeName);
	}

}