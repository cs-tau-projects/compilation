package ast;

public class AstArrayTypedef extends AstNode{
    
    public AstType type;
    
    public AstArrayTypedef(AstType type) {
        this.type = type;
    }

    public void printMe() {
        System.out.print("AST NODE ARRAY TYPEDEF\n");
        if (type != null) type.printMe();
        
        AstGraphviz.getInstance().logNode(
            serialNumber,
            "ARRAY TYPEDEF\n");
        
        if (type != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        }
    }
}