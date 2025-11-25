package ast;

public class AstExpNew extends AstNode
{
    public AstExp exp;
    public AstType type;

    public AstExpNew(AstExp exp, AstType type)
    {
        serialNumber = AstNode.getFreshSerialNumber();
        this.exp = exp;
        this.type = type;
    }

    public void printMe(){
        System.out.println("AST NODE NEW EXPRESSION");
        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("NEW(%s)", type.toString()));
    }
}
