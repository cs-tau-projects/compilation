package ast;

public class AstTypeExp extends AstExp
{
    public AstType t;
    public AstExp  e;

    public AstTypeExp(AstType type, AstExp exp)
    {
        this.t = type;
        this.e = exp;
    }

    @Override
    public void printMe()
    {
        serialNumber = AstNode.getFreshSerialNumber();
        t.printMe();
        e.printMe();
    }
}