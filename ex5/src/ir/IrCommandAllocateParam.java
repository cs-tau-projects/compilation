package ir;

public class IrCommandAllocateParam extends IrCommand {
    public String varName;
    public int scopeOffset;
    public int paramIndex;
    public int numParams;

    public IrCommandAllocateParam(String varName, int scopeOffset, int paramIndex, int numParams) {
        this.varName = varName;
        this.scopeOffset = scopeOffset;
        this.paramIndex = paramIndex;
        this.numParams = numParams;
    }

    public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
        int offset = 4 * numParams - 4 * paramIndex + 8; // Caller pushes left to right (store first, then sub sp), plus 8 bytes for saved $fp and $ra
        gen.allocateParam(scopeOffset, offset);
    }
}
