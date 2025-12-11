package ast;

import types.*;
import symboltable.*;

public class AstExpNew extends AstExp
{
    public AstExp exp;  // can be null for simple new Type
    public AstType type;

    // Constructor for: new Type
    public AstExpNew(AstType type, int lineNumber)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.exp = null;
        this.type = type;
        this.lineNumber = lineNumber;
    }

    // Constructor for: new Type[exp]
    public AstExpNew(AstType type, AstExp exp, int lineNumber)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.exp = exp;
        this.type = type;
        this.lineNumber = lineNumber;
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

    /********************************************************/
    /* Semantic analysis for new expression                */
    /* Handles: new Type (for classes)                     */
    /*          new Type[exp] (for arrays)                 */
    /********************************************************/
    public Type semantMe() throws SemanticException
    {
        Type t = null;

        /****************************/
        /* [1] Check if type exists */
        /****************************/
        t = SymbolTable.getInstance().find(type.typeName);
        if (t == null)
        {
            throw new SemanticException("non existing type " + type.typeName, lineNumber);
        }

        /****************************/
        /* [2] Check if type is void */
        /****************************/
        if (t instanceof TypeVoid)
        {
            throw new SemanticException("cannot instantiate void type", lineNumber);
        }

        /************************************************/
        /* [3] Handle array allocation: new Type[exp]  */
        /************************************************/
        if (exp != null)
        {
            // This is array allocation: new Type[exp]
            Type expType = exp.semantMe();

            // Check that subscript expression is int
            if (expType != TypeInt.getInstance())
            {
                throw new SemanticException("array size must be int", lineNumber);
            }

            // Check for constant non-positive size (must be > 0)
            if (exp instanceof AstExpInt)
            {
                AstExpInt sizeExp = (AstExpInt) exp;
                if (sizeExp.value <= 0)
                {
                    throw new SemanticException("array size must be > 0", lineNumber);
                }
            }

            // Return an array type with element type t
            // Note: We create an anonymous array type here
            return new TypeArray("array of " + t.name, t);
        }

        /************************************************/
        /* [4] Handle class allocation: new Type       */
        /************************************************/
        else
        {
            // This is class allocation: new Type
            // Type must be a class
            if (!t.isClass())
            {
                throw new SemanticException("can only instantiate class types with 'new'", lineNumber);
            }

            return t;
        }
    }
}
