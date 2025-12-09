package types;

/**
 * Utility class for type checking and type compatibility operations.
 * Centralizes common type checking logic used throughout semantic analysis.
 */
public class TypeUtils {
    
    /******************************************************************/
    /* Check if sourceType can be assigned to targetType             */
    /* According to L language semantics:                            */
    /* - Exact type match                                            */
    /* - Subclass can be assigned to superclass                      */
    /* - nil can be assigned to class or array types                 */
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
}

