package ir;

import types.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to compute class memory layout.
 * Fields are laid out contiguously, 4 bytes each.
 * Inherited fields come first (in declaration order),
 * then the class's own fields.
 *
 * Object layout:
 *   offset 0: vtable pointer
 *   offset 4: first field
 *   offset 8: second field
 *   ...
 */
public class ClassLayout {

    /**
     * Get all fields of a class (including inherited), in memory order.
     * Parent fields first, then own fields.
     */
    public static List<String> getFieldsInOrder(TypeClass tc) {
        List<String> fields = new ArrayList<>();
        collectFields(tc, fields);
        return fields;
    }

    private static void collectFields(TypeClass tc, List<String> fields) {
        if (tc == null) return;
        collectFields(tc.father, fields);

        // Members are stored in reverse order in TypeList
        List<Type> ownMembers = new ArrayList<>();
        for (TypeList it = tc.dataMembers; it != null; it = it.tail) {
            ownMembers.add(it.head);
        }

        for (int i = ownMembers.size() - 1; i >= 0; i--) {
            Type m = ownMembers.get(i);
            if (m instanceof TypeField) {
                if (!fields.contains(m.name)) {
                    fields.add(m.name);
                }
            }
        }
    }

    /**
     * Get the byte offset of a field in a class.
     * Offset 0 is the vtable pointer. Fields start at offset 4.
     */
    public static int getFieldOffset(TypeClass tc, String fieldName) {
        List<String> fields = getFieldsInOrder(tc);
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).equals(fieldName)) {
                return (i + 1) * 4; // +1 for vptr
            }
        }
        return -1;
    }

    /**
     * Get total object size in bytes (vptr + fields).
     */
    public static int getObjectSize(TypeClass tc) {
        int fieldCount = getFieldsInOrder(tc).size();
        return (fieldCount + 1) * 4; // +1 for vtable pointer
    }

    /**
     * Get the byte offset of a method in the vtable.
     */
    public static int getMethodOffset(TypeClass tc, String methodName) {
        List<String> vtable = getMethodLabels(tc);
        for (int i = 0; i < vtable.size(); i++) {
            if (vtable.get(i).endsWith("_" + methodName)) {
                return i * 4;
            }
        }
        return -1;
    }

    /**
     * Build vtable method labels for a class (same logic as AstDecClass.buildVtable).
     */
    public static List<String> getMethodLabels(TypeClass tc) {
        List<String> methods = new ArrayList<>();
        if (tc == null) return methods;

        if (tc.father != null) {
            methods.addAll(getMethodLabels(tc.father));
        }

        List<Type> ownMembers = new ArrayList<>();
        for (TypeList it = tc.dataMembers; it != null; it = it.tail) {
            ownMembers.add(it.head);
        }
        java.util.Collections.reverse(ownMembers);

        for (Type member : ownMembers) {
            if (member instanceof TypeFunction) {
                String funcName = member.name;
                String methodLabel = "Method_" + tc.name + "_" + funcName;

                boolean overridden = false;
                for (int i = 0; i < methods.size(); i++) {
                    if (methods.get(i).endsWith("_" + funcName)) {
                        methods.set(i, methodLabel);
                        overridden = true;
                        break;
                    }
                }
                if (!overridden) {
                    methods.add(methodLabel);
                }
            }
        }
        return methods;
    }
}
