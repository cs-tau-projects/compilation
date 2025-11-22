package ast;
import java.io.PrintWriter;

public class AstType extends AstNode
{
	public static final int TYPE_INT = 0;
	public static final int TYPE_STRING = 1;
	public static final int TYPE_VOID = 2;

	public int type;
	public String className;
	
	// Constructor for built-in types (TYPE_INT, TYPE_STRING, TYPE_VOID)
	public AstType(int type)
	{
		this.type = type;
		this.className = null;
	}
	
	// Constructor for custom class types
	public AstType(String className)
	{
		this.type = -1; // -1 indicates a identifier
		this.className = className;
	}
	
	public void printMe()
	{
		if (className != null) {
			System.out.print("TYPE: " + className + "\n");
		} else {
			String typeName = "";
			switch(type)
			{
				case TYPE_INT:
					typeName = "INT";
					break;
				case TYPE_STRING:
					typeName = "STRING";
					break;
				case TYPE_VOID:
					typeName = "VOID";
					break;
				default:
					typeName = "IDENTIFIER";
			}
			System.out.print("TYPE: " + typeName + "\n");
		}
	}
}