package ast;

import types.*;

public class AstDec extends AstNode
{
    public AstNode decNode;

    public AstDec(AstNode decNode) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.decNode = decNode;
    }

    public void printMe() {
        System.out.print("AST NODE DEC\n");
        if (decNode != null) decNode.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "DEC");
        if (decNode != null) AstGraphviz.getInstance().logEdge(serialNumber, decNode.serialNumber);
    }

    /***********************************************/
	/* The default semantic action for an AST node */
	/***********************************************/
	public Type semantMe() throws SemanticException
	{
		/******************************************/
		/* Delegate to the actual declaration node */
		/******************************************/
		if (decNode != null)
		{
			return decNode.semantMe();
		}
		return null;
	}
}