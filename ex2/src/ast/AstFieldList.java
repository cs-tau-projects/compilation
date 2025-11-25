package ast;

public class AstFieldList extends AstNode {
    public AstField         head;
    public AstFieldList     tail;

    public AstFieldList(AstField field, AstFieldList fields) {
        serialNumber = AstNode.getFreshSerialNumber();
        this.fields = fields;
    }

    @Override
    public void PrintMe() {
        System.out.print("AST NODE FIELD LIST\n");
        if (head != null) head.PrintMe();
        if (tail != null) tail.PrintMe();
    }
}