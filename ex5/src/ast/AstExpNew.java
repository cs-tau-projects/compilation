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

    // semantic analysis
    public Type semantMe() throws SemanticException
    {
        Type t = null;

        // type existence check
        t = SymbolTable.getInstance().find(type.typeName);
        if (t == null)
        {
            throw new SemanticException("non existing type " + type.typeName, lineNumber);
        }

        // void check
        if (t instanceof TypeVoid)
        {
            throw new SemanticException("cannot instantiate void type", lineNumber);
        }

        // array case
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
            Integer constantSize = exp.tryEvaluateConstant();
            if (constantSize != null && constantSize <= 0)
            {
                throw new SemanticException("array size must be > 0", lineNumber);
            }

            // Return an array type with element type t
            // Note: We create an anonymous array type here
            return new TypeArray("array of " + t.name, t);
        }

        // class case
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

    public temp.Temp irMe()
    {
        temp.Temp dst = temp.TempFactory.getInstance().getFreshTemp();
        if (exp != null)
        {
            temp.Temp sizeTemp = exp.irMe();
            ir.Ir.getInstance().AddIrCommand(new ir.IrCommandNewArray(dst, sizeTemp));
        }
        else
        {
            Type t = SymbolTable.getInstance().find(type.typeName);
            int size = 8; // Default
            if (t instanceof TypeClass) {
                size = types.TypeUtils.getClassSize((TypeClass) t);
            }
            ir.Ir.getInstance().AddIrCommand(new ir.IrCommandNewObject(dst, type.typeName, size));
            if (t instanceof TypeClass) {
                initializeFields((TypeClass) t, dst);
            }
        }
        return dst;
    }

    private void initializeFields(TypeClass tc, temp.Temp objAddr) {
        if (tc.father != null) {
            initializeFields(tc.father, objAddr);
        }
        for (types.TypeList it = tc.dataMembers; it != null; it = it.tail) {
            if (it.head instanceof types.TypeField) {
                types.TypeField field = (types.TypeField) it.head;
                if (field.initExp != null) {
                    temp.Temp valTemp = field.initExp.irMe();
                    int offset = types.TypeUtils.getFieldOffset(tc, field.name);
                    ir.Ir.getInstance().AddIrCommand(new ir.IrCommandFieldSet(objAddr, offset, valTemp));
                }
            }
        }
    }
}
