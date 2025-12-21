package ast;

import java.io.PrintWriter;

/* changed from: public class AstTypeExp extends AstNode - type missmatch*/
public class AstTypeExp extends AstExp
{
    public AstType t;
    public AstExp  e;

    public AstTypeExp(AstType type, AstExp exp)
    {
        this.t = type;
        this.e = exp;
    }

    public void printMe()
    {
        serialNumber = AstNode.getFreshSerialNumber();
        t.printMe();
        e.printMe();
    }
}