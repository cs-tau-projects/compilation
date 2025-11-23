package ast;

public class AstParametersList extends AstNode{
    AstParam param;
    List<AstParam> params;

    public AstParametersList(AstParam param, AstParametersList params){
        serialNumber = AstNode.getFreshSerialNumber();
        this.param = param;
        this.params = params;
    }

    public void printMe(){
        System.out.format("AST PARAMETERS LIST NODE:\n");
        param.printMe();
        if (params != null) {
            params.printMe();
        }
    }
}