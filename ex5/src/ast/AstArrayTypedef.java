package ast;

import types.*;
import symboltable.*;

public class AstArrayTypedef extends AstNode{
    String id;
    public AstType type;
    
    public AstArrayTypedef(String id, AstType type, int lineNumber) {
        serialNumber = AstNode.getFreshSerialNumber();
        this.id = id;
        this.type = type;
        this.lineNumber = lineNumber;
    }

    public void printMe() {
        System.out.print("AST NODE ARRAY TYPEDEF\n");
        System.out.print("ID: " + id + "\n");
        if (type != null) type.printMe();

        AstGraphviz.getInstance().logNode(
            serialNumber,
            "ARRAY TYPEDEF\n");

        if (type != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        }
    }

    /********************************************************/
    /* Semantic analysis for array type definition         */
    /* Validates element type and registers array type     */
    /********************************************************/
    public Type semantMe() throws SemanticException
    {
        Type elementType = null;

        /************************************/
        /* [0] Check for reserved keyword   */
        /************************************/
        TypeUtils.checkNotReservedKeyword(id, lineNumber);

        /**************************************/
        /* [1] Check if array name already exists */
        /**************************************/
        if (SymbolTable.getInstance().find(id) != null)
        {
            throw new SemanticException("array type " + id + " already exists", lineNumber);
        }

        /****************************/
        /* [2] Check if element type exists */
        /****************************/
        elementType = SymbolTable.getInstance().find(type.typeName);
        if (elementType == null)
        {
            throw new SemanticException("non existing type " + type.typeName, lineNumber);
        }

        /******************************************/
        /* [3] Check that element type is not void */
        /******************************************/
        if (elementType instanceof TypeVoid)
        {
            throw new SemanticException("array cannot have void element type", lineNumber);
        }

        /************************************************/
        /* [4] Create and register the array type      */
        /************************************************/
        TypeArray arrayType = new TypeArray(id, elementType);
        SymbolTable.getInstance().enter(id, arrayType);

        /************************************************************/
        /* [5] Return value is irrelevant for type declarations    */
        /************************************************************/
        return null;
    }
}