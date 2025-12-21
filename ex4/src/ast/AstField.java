package ast;

public class AstField extends AstNode {
    public AstDecVar    decVar;
    public AstDecFunc   decFunc;

    public AstField(AstDecVar decVar, int lineNumber) {
        serialNumber = AstNode.getFreshSerialNumber();
        this.decVar = decVar;
        this.lineNumber = lineNumber;
    }

    public AstField(AstDecFunc decFunc, int lineNumber) {
        serialNumber = AstNode.getFreshSerialNumber();
        this.decFunc = decFunc;
        this.lineNumber = lineNumber;
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