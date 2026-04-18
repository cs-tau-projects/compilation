package types;

import ast.*;
import symboltable.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for type checking and type compatibility operations.
 * Centralizes common type checking logic used throughout semantic analysis.
 */
public class TypeUtils {

    // reserved keywords
    private static final Set<String> RESERVED_KEYWORDS = new HashSet<>();
    static {
        RESERVED_KEYWORDS.add("int");
        RESERVED_KEYWORDS.add("string");
        RESERVED_KEYWORDS.add("void");
        RESERVED_KEYWORDS.add("if");
        RESERVED_KEYWORDS.add("else");
        RESERVED_KEYWORDS.add("while");
        RESERVED_KEYWORDS.add("return");
        RESERVED_KEYWORDS.add("new");
        RESERVED_KEYWORDS.add("nil");
        RESERVED_KEYWORDS.add("class");
        RESERVED_KEYWORDS.add("extends");
        RESERVED_KEYWORDS.add("array");
        RESERVED_KEYWORDS.add("PrintInt");
        RESERVED_KEYWORDS.add("PrintString");
    }

    // check if reserved
    public static boolean isReservedKeyword(String name)
    {
        return RESERVED_KEYWORDS.contains(name);
    }

    // check keyword policy
    public static void checkNotReservedKeyword(String name, int lineNumber) throws SemanticException
    {
        if (isReservedKeyword(name))
        {
            throw new SemanticException("identifier " + name + " is a reserved keyword", lineNumber);
        }
    }
    
    // type assignment check
    public static boolean canAssignType(Type targetType, Type sourceType)
    {
        // Exact match
        if (targetType == sourceType)
        {
            return true;
        }

        // nil can be assigned to class or array types
        if (sourceType.name != null && sourceType.name.equals("nil"))
        {
            return targetType.isClass() || targetType.isArray();
        }

        // Subclass can be assigned to superclass
        if (targetType.isClass() && sourceType.isClass())
        {
            TypeClass sourceClass = (TypeClass) sourceType;
            TypeClass targetClass = (TypeClass) targetType;
            return isSubclassOf(sourceClass, targetClass);
        }

        // Array assignment: only anonymous arrays (from new T[]) can be assigned to named array types
        // Two different named array types are NOT compatible (nominal typing per Table 5)
        if (targetType.isArray() && sourceType.isArray())
        {
            TypeArray targetArray = (TypeArray) targetType;
            TypeArray sourceArray = (TypeArray) sourceType;

            // Anonymous arrays (from new T[]) have names like "array of T"
            // They can be assigned to named array types if element types match
            if (sourceArray.name != null && sourceArray.name.startsWith("array of "))
            {
                return sourceArray.elementType == targetArray.elementType;
            }

            // Two different named array types are NOT compatible (nominal typing)
            // Same named types would have been caught by targetType == sourceType above
            return false;
        }

        return false;
    }

    // subclass check
    public static boolean isSubclassOf(TypeClass child, TypeClass parent)
    {
        TypeClass current = child.father;
        while (current != null)
        {
            if (current == parent)
            {
                return true;
            }
            current = current.father;
        }
        return false;
    }

    // find member in class
    public static Type findMemberInClassHierarchy(TypeClass classType, String memberName)
    {
        TypeClass currentClass = classType;
        while (currentClass != null)
        {
            for (TypeList it = currentClass.dataMembers; it != null; it = it.tail)
            {
                if (it.head.name.equals(memberName))
                {
                    return it.head;
                }
            }
            currentClass = currentClass.father;
        }
        return null;
    }

    public static int getFieldOffset(TypeClass classType, String fieldName) {
        java.util.List<TypeField> fields = getFullFieldList(classType);
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).name.equals(fieldName)) {
                return (i + 1) * 4; // +1 for vtable at offset 0
            }
        }
        return -1;
    }

    public static int getClassSize(TypeClass classType) {
        java.util.List<TypeField> fields = getFullFieldList(classType);
        return (fields.size() + 1) * 4; // +1 for vtable at offset 0
    }

    private static java.util.List<TypeField> getFullFieldList(TypeClass classType) {
        java.util.List<TypeField> fields = new java.util.ArrayList<>();
        if (classType.father != null) {
            fields.addAll(getFullFieldList(classType.father));
        }
        
        // Members are stored in reverse order in TypeList (newest first)
        java.util.List<Type> ownMembers = new java.util.ArrayList<>();
        for (TypeList it = classType.dataMembers; it != null; it = it.tail) {
            ownMembers.add(it.head);
        }
        java.util.Collections.reverse(ownMembers);

        for (Type member : ownMembers) {
            if (member instanceof TypeField) {
                fields.add((TypeField) member);
            }
        }
        return fields;
    }

    // build parameter list
    public static TypeList buildParameterTypeList(AstParametersList params, int lineNumber) throws SemanticException
    {
        if (params == null)
        {
            return null;
        }

        // Look up parameter type
        Type paramType = SymbolTable.getInstance().find(params.head.type.typeName);
        if (paramType == null)
        {
            throw new SemanticException("non existing parameter type " + params.head.type.typeName, lineNumber);
        }

        // Check that parameter type is not void
        if (paramType instanceof TypeVoid)
        {
            throw new SemanticException("parameter cannot have void type", lineNumber);
        }

        // Recursively process tail to maintain order
        TypeList tailTypeList = buildParameterTypeList(params.tail, lineNumber);

        // Build list with head first, then tail (correct order)
        return new TypeList(paramType, tailTypeList);
    }
}

