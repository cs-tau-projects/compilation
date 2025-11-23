
package ast;

public class AstDecFunc extends AstNode
{
    public AstType returnType;
    public String funcName;
    public AstDecList params;
    public AstStmts body;

    public AstDecFunc(AstType returnType, String funcName, AstDecList params, AstStmts body)
    {
        serialNumber = AstNode.getFreshSerialNumber();
        this.returnType = returnType;
        this.funcName = funcName;
        this.params = params;
        this.body = body;
    }

    @Override
    public void printMe(){
        System.out.format("AST DEC FUNC NODE: %s\n", funcName);
        if (returnType != null) returnType.printMe();
        if (params != null) params.printMe();
        if (body != null) body.printMe();
    }

}