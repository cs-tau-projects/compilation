package ast;

public class AstParametersList extends AstNode{
    public AstParam head;
    public AstParametersList tail;

    public AstParametersList(AstParam head, AstParametersList tail, int lineNumber){
        serialNumber = AstNodeSerialNumber.getFresh();
        this.head = head;
        this.tail = tail;
        this.lineNumber = lineNumber;
    }

    @Override
    public void printMe(){
        System.out.format("AST PARAMETERS LIST NODE:\n");
        if (head != null) head.printMe();
        if (tail != null) tail.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "PARAMS\nLIST");

        if (head != null) AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
        if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
    }
}