package ast;

public class AstClassDec extends AstNode{
    String id;
    String parentId; // can be null
    List<AstDec> fields;

    public AstClassDec(String id, String parentId, List<AstDec> fields){
        serialNumber = AstNode.getFreshSerialNumber();
        this.id = id;
        this.parentId = parentId;
        this.fields = fields;
    }
}