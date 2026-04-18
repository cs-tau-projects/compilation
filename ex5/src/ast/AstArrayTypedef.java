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

    // semant
    public Type semantMe() throws SemanticException
    {
        Type elementType = null;

        // check reserved
        TypeUtils.checkNotReservedKeyword(id, lineNumber);

        // check if name exists
        if (SymbolTable.getInstance().find(id) != null)
        {
            throw new SemanticException("array type " + id + " already exists", lineNumber);
        }

        // check element type
        elementType = SymbolTable.getInstance().find(type.typeName);
        if (elementType == null)
        {
            throw new SemanticException("non existing type " + type.typeName, lineNumber);
        }

        // check not void
        if (elementType instanceof TypeVoid)
        {
            throw new SemanticException("array cannot have void element type", lineNumber);
        }

        // register
        TypeArray arrayType = new TypeArray(id, elementType);
        SymbolTable.getInstance().enter(id, arrayType);

        return null;
    }
}