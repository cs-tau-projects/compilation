
package ast;

public class AstDecFunc extends AstNode
{
    public AstType returnType;
    public String funcName;
    /* below was: "public AstDecList params; public AstStmts body;" - But arams are stored as AstDecList,but CUP produces AstParametersList. 
    Body is stored as AstStmts (we dont even have this class), but CUP produces AstStmtList. */
    public AstParametersList params;   
    public AstStmtList body;           
    /*changed below as well, was declared the wrong types as above... (AstDecList params and AstStmts body) */
    public AstDecFunc(AstType returnType, String funcName, AstParametersList params, AstStmtList body)
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