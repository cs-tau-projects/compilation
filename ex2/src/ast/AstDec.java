package ast;

public class AstDec extends AstNode
{
    public AstNode decNode;

    /* was public AstDec(AstNode decFunc) - changed because we store the child in decNode not in decfunc (idk why it was written before) */
    public AstDec(AstNode decNode) {
        serialNumber = AstNode.getFreshSerialNumber();
        this.decNode = decNode;
    }

    public void printMe() {
        System.out.print("AST NODE DEC\n");
        if (decNode != null) decNode.printMe();
        AstGraphViz.getInstance().logNode(serialNumber, "DEC");
        if (decNode != null) AstGraphViz.getInstance().logEdge(serialNumber, decNode.serialNumber);
    }
}