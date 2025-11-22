package ast;

import java.io.PrintWriter;

public class AstTypeExp extends AstNode
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
        System.out.print("TYPE EXP:\n");
        t.printMe();
        e.printMe();
    }
}