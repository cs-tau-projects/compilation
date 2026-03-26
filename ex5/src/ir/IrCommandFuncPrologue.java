package ir;

public class IrCommandFuncPrologue extends IrCommand {
    public int numLocals;

    public IrCommandFuncPrologue(int numLocals) {
        this.numLocals = numLocals;
    }

    // Keep backward-compatible default
    public IrCommandFuncPrologue() {
        this.numLocals = 0;
    }

    @Override
    public java.util.List<temp.Temp> getUsedTemps() {
        return new java.util.ArrayList<>();
    }

    @Override
    public java.util.List<temp.Temp> getDefinedTemps() {
        return new java.util.ArrayList<>();
    }

    @Override
    public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
        gen.resetLocals();
        // Push return address
        gen.emitInstruction("subu", "$sp", "$sp", "4");
        gen.emitInstruction("sw", "$ra", "0($sp)");
        // Push old frame pointer
        gen.emitInstruction("subu", "$sp", "$sp", "4");
        gen.emitInstruction("sw", "$fp", "0($sp)");
        // Set frame pointer to current stack pointer
        gen.emitInstruction("move", "$fp", "$sp");
        // Allocate space for local variables
        if (numLocals > 0) {
            gen.emitInstruction("subu", "$sp", "$sp", String.valueOf(numLocals * 4));
        }
    }
}
