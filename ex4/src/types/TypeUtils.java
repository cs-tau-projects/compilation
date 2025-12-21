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

    /******************************************************************/
    /* Set of reserved keywords that cannot be used as identifiers    */
    /******************************************************************/
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

    /******************************************************************/
    /* Check if a name is a reserved keyword                          */
    /******************************************************************/
    public static boolean isReservedKeyword(String name)
    {
        return RESERVED_KEYWORDS.contains(name);
    }

    /******************************************************************/
    /* Check identifier and throw exception if it's a reserved keyword*/
    /******************************************************************/
    public static void checkNotReservedKeyword(String name, int lineNumber) throws SemanticException
    {
        if (isReservedKeyword(name))
        {
            throw new SemanticException("identifier " + name + " is a reserved keyword", lineNumber);
        }
    }
    
    /******************************************************************/
    /* Check if sourceType can be assigned to targetType             */
    /* According to L language semantics:                            */
    /* - Exact type match                                            */
    /* - Subclass can be assigned to superclass                      */
    /* - nil can be assigned to class or array types                 */
    /* - For arrays: element types must match (for new T[] case)     */
    /******************************************************************/
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

    /******************************************************************/
    /* Check if child is a subclass of parent                        */
    /* Walks up the inheritance chain to find if child derives from  */
    /* parent                                                         */
    /******************************************************************/
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

    /******************************************************************/
    /* Find a member (field or method) in class hierarchy            */
    /* Searches the class and all parent classes for a member with   */
    /* the given name                                                 */
    /******************************************************************/
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

    /******************************************************************/
    /* Build parameter type list in correct order                    */
    /* Recursively processes parameters to maintain proper order     */
    /* Used by both function and method declarations                 */
    /******************************************************************/
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

