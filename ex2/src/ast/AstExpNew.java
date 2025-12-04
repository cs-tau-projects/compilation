package ast;

public class AstExpNew extends AstExp
{
    public AstExp exp;  // can be null for simple new Type
    public AstType type;

    // Constructor for: new Type
    public AstExpNew(AstType type)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.exp = null;
        this.type = type;
    }

    // Constructor for: new Type[exp]
    public AstExpNew(AstType type, AstExp exp)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.exp = exp;
        this.type = type;
    }

    public void printMe(){
        System.out.println("AST NODE NEW EXPRESSION");
        if (type != null) type.printMe();
        if (exp != null) exp.printMe();

        String label = (exp != null) ?
            String.format("NEW\n%s[...]", type.typeName) :
            String.format("NEW\n%s", type.typeName);

        AstGraphviz.getInstance().logNode(serialNumber, label);

        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }
}
