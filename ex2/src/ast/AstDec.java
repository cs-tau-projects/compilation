package ast;

public class AstDec extends AstNode
{
    public AstNode decNode;

    public AstDec(AstNode decFunc) {
        serialNumber = AstNode.getFreshSerialNumber();
        this.decFunc = decFunc;
    }

    public void printMe() {
        System.out.print("AST NODE DEC\n");
        if (decNode != null) decNode.printMe();
        AstGraphViz.getInstance().logNode(serialNumber, "DEC");
        if (decNode != null) AstGraphViz.getInstance().logEdge(serialNumber, decNode.serialNumber);
    }
}