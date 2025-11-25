package ast;

public class AstField extends AstNode {
    public AstDecVar    decVar;
    public AstDecFunc   decFunc;

    public AstField(AstDecVar decVar) {
        serialNumber = AstNode.getFreshSerialNumber();
        this.decVar = decVar;
    }

    public AstField(AstDecFunc decFunc) {
        serialNumber = AstNode.getFreshSerialNumber();
        this.decFunc = decFunc;
    }

    public void printMe() {
        System.out.print("AST NODE FIELD\n");
        if (decVar != null) {
            decVar.printMe();
        }
        if (decFunc != null) {
            decFunc.printMe();
        }
    }
}