package ast;

public class AstFuncDec extends AstDec{
    AstType returnType;
    String id;
    List<AstVarDec> params;
    AstStmtList body;

    public AstFuncDec(AstType returnType, String id, List<AstVarDec> params, AstStmtList body){
        this.returnType = returnType;
        this.id = id;
        this.params = params;
        this.body = body;
    }
}