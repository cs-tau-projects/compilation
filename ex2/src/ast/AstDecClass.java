package ast;

public class AstDecClass extends AstNode{
    public String id;
    public String parentId; // can be null
    public AstFieldList fields;

    public AstDecClass(String id, String parentId, AstFieldList fields){
        serialNumber = AstNodeSerialNumber.getFresh();
        this.id = id;
        this.parentId = parentId;
        this.fields = fields;
    }

    public void printMe(){
        System.out.format("AST CLASS DEC NODE: %s\n", id);
        if (parentId != null) {
            System.out.format("EXTENDS: %s\n", parentId);
        }
        if (fields != null) fields.printMe();

        String label = (parentId != null) ?
            String.format("CLASS\n%s\nEXTENDS %s", id, parentId) :
            String.format("CLASS\n%s", id);

        AstGraphviz.getInstance().logNode(serialNumber, label);

        if (fields != null) AstGraphviz.getInstance().logEdge(serialNumber, fields.serialNumber);
    }
}