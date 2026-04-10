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
        int offset = 80 + 4 * paramIndex;
        gen.allocateParam(scopeOffset, offset);
    }
}
