package ast;

public class AstArrayTypedef extends AstNode{
    String id;
    public AstType type;
    
    public AstArrayTypedef(String id, AstType type) {
        serialNumber = AstNode.getFreshSerialNumber();
        this.id = id;
        this.type = type;
    }

    public void printMe() {
        System.out.print("AST NODE ARRAY TYPEDEF\n");
        System.out.print("ID: " + id + "\n");
        if (type != null) type.printMe();
        
        AstGraphviz.getInstance().logNode(
            serialNumber,
            "ARRAY TYPEDEF\n");
        
        if (type != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        }
    }
}