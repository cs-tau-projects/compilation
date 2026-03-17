package ast;

import temp.*;
import types.*;

public class AstDec extends AstNode
{
    public AstNode decNode;

    public AstDec(AstNode decNode, int lineNumber) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.decNode = decNode;
        this.lineNumber = lineNumber;
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

	/***********************************************/
	/* IR generation - delegate to the decNode    */
	/***********************************************/
	public Temp irMe()
	{
		if (decNode != null)
		{
			return decNode.irMe();
		}
		return null;
	}
}