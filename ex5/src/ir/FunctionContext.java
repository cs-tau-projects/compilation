package ir;

/**
 * Tracks the current function context during IR generation.
 * Maintains parameter and local variable offset counters
 * for $fp-relative addressing.
 *
 * Stack layout (after prologue):
 *   +8+4*(n-1)($fp)  = param n (last pushed, highest address)
 *   ...
 *   +8($fp)          = param 0 (first pushed / 'this' for methods)
 *   +4($fp)          = saved $ra
 *    0($fp)          = saved $fp  <-- $fp points here
 *   -4($fp)          = local 0
 *   -8($fp)          = local 1
 *   ...
 */
public class FunctionContext {
    private static FunctionContext current = null;

    private String funcName;
    private int numParams;
    private int localCount;
    private java.util.Map<String, Integer> paramOffsets;
    private java.util.Map<String, Integer> localOffsets;

    public FunctionContext(String funcName, int numParams) {
        this.funcName = funcName;
        this.numParams = numParams;
        this.localCount = 0;
        this.paramOffsets = new java.util.LinkedHashMap<>();
        this.localOffsets = new java.util.LinkedHashMap<>();
    }

    public static void enterFunction(String funcName, int numParams) {
        current = new FunctionContext(funcName, numParams);
    }

    public static void exitFunction() {
        current = null;
    }

    public static FunctionContext getCurrent() {
        return current;
    }

    public static boolean isInFunction() {
        return current != null;
    }

    /**
     * Register a parameter. Params are at +8, +12, +16, etc. from $fp.
     */
    public void addParam(String name, int paramIndex) {
        int offset = 8 + paramIndex * 4;
        paramOffsets.put(name, offset);
    }

    /**
     * Register a local variable. Locals are at -4, -8, -12, etc. from $fp.
     */
    public int addLocal(String name) {
        localCount++;
        int offset = -localCount * 4;
        localOffsets.put(name, offset);
        return offset;
    }

    public VarId.Kind getKind(String name) {
        if (paramOffsets.containsKey(name)) return VarId.Kind.PARAM;
        if (localOffsets.containsKey(name)) return VarId.Kind.LOCAL;
        return VarId.Kind.GLOBAL;
    }

    public int getFpOffset(String name) {
        if (paramOffsets.containsKey(name)) return paramOffsets.get(name);
        if (localOffsets.containsKey(name)) return localOffsets.get(name);
        return 0;
    }

    public String getFuncName() { return funcName; }
    public int getNumParams() { return numParams; }
    public int getLocalCount() { return localCount; }
}
