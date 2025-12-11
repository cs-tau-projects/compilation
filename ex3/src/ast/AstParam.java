
package ast;

public class AstParam extends AstNode{
    public AstType type;
    public String id;

    public AstParam(AstType type, String id, int lineNumber){
        serialNumber = AstNode.getFreshSerialNumber();
        this.type = type;
        this.id = id;
        this.lineNumber = lineNumber;
    }

    public void printMe(){
        System.out.format("AST PARAM NODE: %s %s\n", type.typeName, id);
    }
}