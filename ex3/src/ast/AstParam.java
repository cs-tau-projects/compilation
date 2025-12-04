
package ast;

public class AstParam extends AstNode{
    AstType type;
    String id;

    public AstParam(AstType type, String id){
        serialNumber = AstNode.getFreshSerialNumber();
        this.type = type;
        this.id = id;
    }

    public void printMe(){
        System.out.format("AST PARAM NODE: %s %s\n", type.typeName, id);
    }
}