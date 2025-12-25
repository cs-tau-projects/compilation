package ast;
import temp.*;

public class AstFieldList extends AstNode {
    public AstField         head;
    public AstFieldList     tail;

    public AstFieldList(AstField head, AstFieldList tail, int lineNumber) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.head = head;
        this.tail = tail;
        this.lineNumber = lineNumber;
    }

    @Override
    public void printMe() {
        System.out.print("AST NODE FIELD LIST\n");
        if (head != null) head.printMe();
        if (tail != null) tail.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "FIELD\nLIST");

        if (head != null) AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
        if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
    }

    @Override
    public Temp irMe() {
        // Probably not needed for ex4, but will be needed in the future
        if (head != null) head.irMe();
        if (tail != null) tail.irMe();

        // UNREACHABLE
        return null;
    }
}