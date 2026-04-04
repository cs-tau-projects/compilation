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
        gen.resetLocals(); 
        
        // Count locals for this function to pre-allocate stack space
        int numLocals = 0;
        java.util.List<IrCommand> commands = Ir.getInstance().getCommands();
        int myIndex = commands.indexOf(this);
        if (myIndex != -1) {
            for (int i = myIndex + 1; i < commands.size(); i++) {
                IrCommand cmd = commands.get(i);
                if (cmd instanceof IrCommandFuncPrologue) break;
                if (cmd instanceof IrCommandAllocate) {
                    if (!((IrCommandAllocate) cmd).isGlobal) {
                        numLocals++;
                    }
                }
            }
        }

        // 1. Save FP
        gen.emitInstruction("subu", "$sp", "$sp", "4");
        gen.emitInstruction("sw", "$fp", "0($sp)");
        // 2. Save RA
        gen.emitInstruction("subu", "$sp", "$sp", "4");
        gen.emitInstruction("sw", "$ra", "0($sp)");
        // 3. Save S0-S7 (32 bytes)
        gen.emitInstruction("subu", "$sp", "$sp", "32");
        for (int i = 0; i < 8; i++) {
            gen.emitInstruction("sw", "$s" + i, (i*4) + "($sp)");
        }
        // 4. Set FP to current SP (top of frame overhead)
        gen.emitInstruction("move", "$fp", "$sp");

        // 5. Pre-allocate space for all local variables
        if (numLocals > 0) {
            gen.emitInstruction("subu", "$sp", "$sp", String.valueOf(numLocals * 4));
        }
    }
}
