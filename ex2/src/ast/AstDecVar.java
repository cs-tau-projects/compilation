package ast;

public class AstDecVar extends AstNode {
    public String id;
    public AstType type;
    public AstExp exp = null;

    public AstDecVar(String id, AstType type) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.id = id;
        this.type = type;
    }

    public AstDecVar(String id, AstType type, AstExp exp) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.id = id;
        this.type = type;
        this.exp = exp;
    }

    public void printMe() {
        System.out.print("AST NODE VAR DEC\n");
        System.out.print("VAR NAME: " + id + "\n");
        if (type != null) type.printMe();
        if (exp != null) exp.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, String.format("VAR DEC\n%s", id));

        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }
}
