package ir;

public class IrCommandFuncPrologue extends IrCommand {
    public IrCommandFuncPrologue() {}

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
        gen.resetLocals(); // Reset the MipsGenerator local offset tracking for the new function
        gen.emitInstruction("sw", "$fp", "0($sp)");
        gen.emitInstruction("sub", "$sp", "$sp", "4");
        gen.emitInstruction("sw", "$ra", "0($sp)");
        gen.emitInstruction("sub", "$sp", "$sp", "4");
        gen.emitInstruction("move", "$fp", "$sp");
    }
}
