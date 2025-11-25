package ast;
import java.io.PrintWriter;

public class AstType extends AstNode
{
	public static final String INT_TYPE = "int";
	public static final String STRING_TYPE = "string";
	public static final String VOID_TYPE = "void";

	public String typeName; // has to be one of the above or an identifier name

	public AstType(String typeName)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.typeName = typeName;
	}

	@Override
	public void printMe(){
		System.out.format("AST TYPE NODE: %s\n", typeName);
		AstGraphviz.getInstance().logNode(serialNumber, String.format("TYPE\n%s", typeName));
	}

}