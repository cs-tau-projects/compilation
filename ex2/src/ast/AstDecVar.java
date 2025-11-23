package ast;

public class AstDecVar extends AstDec {
    public String id;
    public AstType type;
    public AstExp exp = null;

    public AstDecVar(String id, AstType type) {
        serialNumber = AstNode.getFreshSerialNumber();
        this.id = id;
        this.type = type;
    }

    public AstDecVar(String id, AstType type, AstExp exp) {
        serialNumber = AstNode.getFreshSerialNumber();
        this.id = id;
        this.type = type;
        this.exp = exp;
    }

    public void printMe() {
        System.out.print("AST NODE VAR DEC\n");
        System.out.print("VAR NAME: " + name + "\n");
        if (type != null) type.printMe();
        if (exp != null) exp.printMe();
    }
}
